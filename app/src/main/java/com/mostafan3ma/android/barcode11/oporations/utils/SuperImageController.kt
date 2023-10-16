package com.mostafan3ma.android.barcode11.oporations.utils
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.inject.Inject

class SuperImageController
@Inject
constructor() {
    companion object{
        const val IMAGES_FILE="Images File"
    }

    /**
     * - it initialize in onResume fun of the host
     * - else it wil still null

     */
    var returnedUri: Uri? = null
    private lateinit var registrar: ActivityResultLauncher<String>


    /**
     * - register the ActivityResultLauncher<String>
     * - set listener and get the uri
     */
    fun register(host: Any) {
        when (host) {
            is ComponentActivity -> {
                this.registrar =
                    host.registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                        // Handle the returned Uri
                        returnedUri = uri!!
                    }
            }
            is Fragment -> {
                this.registrar =
                    host.registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                        // Handle the returned Uri
                        if (uri !=null) {
                            returnedUri = uri
                        }
                    }
            }
            else -> {

            }
        }
    }

    /**
     * launch the registrar and open the image chooser
     */
    fun launchRegistrar() {
        registrar.launch("image/*")
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun getBitmapFromRegister(context: Context): Bitmap {
        val source: ImageDecoder.Source =
            ImageDecoder.createSource(context.contentResolver, returnedUri!!)
        return ImageDecoder.decodeBitmap(source)
    }


    /**
     * works in an Activity(ComponentActivity)
     * use it to open Image chooser and let the user to pick image from the gallery and then display it on an imageView
     */
    @RequiresApi(Build.VERSION_CODES.P)
    fun pickImgFromActivityAndDisplay(
        component: ComponentActivity,
        img: ImageView,
        btn: Button
    ) {
        val getContent =
            component.registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                // Handle the returned Uri
                val source: ImageDecoder.Source =
                    ImageDecoder.createSource(component.contentResolver, returnedUri!!)
                img.setImageBitmap(ImageDecoder.decodeBitmap(source))
            }

        btn.setOnClickListener {
            //            chooseImage()
            getContent.launch("image/*")
        }
    }


    /**
     * works in a Fragment
    use it to open Image chooser and let the user to pick image from the gallery and then display it on an imageView
     **/
    @RequiresApi(Build.VERSION_CODES.P)
    fun pickImgFromFragmentAndDisplay(
        component: Fragment,
        context: Context,
        img: ImageView,
        btn: Button
    ) {
        val getContent =
            component.registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                // Handle the returned Uri
                val source: ImageDecoder.Source =
                    ImageDecoder.createSource(context.contentResolver, returnedUri!!)
                img.setImageBitmap(ImageDecoder.decodeBitmap(source))
            }

        btn.setOnClickListener {
            //            chooseImage()
            getContent.launch("image/*")
        }
    }


    /**
     *-  download Image from Uri
     *-  uses Coil library
     *-  implementation("io.coil-kt:coil:2.1.0")
     * - run inside CoroutineScope,onEach,launchIn
     * - copy attached code ->>>>
     */
    suspend fun downloadUriToBitmap(context: Context, uri: String): Flow<DataState<Bitmap>> = flow {
        emit(DataState.Loading)
        val loading: ImageLoader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(uri)
            .build()

        val result: Drawable? = null
        try {
            val result: Drawable = (loading.execute(request) as SuccessResult).drawable
            emit(DataState.Success((result as BitmapDrawable).bitmap))
        } catch (e: Exception) {
            Log.d("imageError", "downloadToBitmap: ${e.message}")
            emit(DataState.Error(e))
        }

//        lifecycle.coroutineScope.launch{
//            val uri="pest Uri"
//            imagePicker.downloadUriToBitmap(this@MainActivity,uri).onEach { dataState: DataState<Bitmap> ->
//                when(dataState){
//                    is DataState.Success->{
//                        binding.img.setImageBitmap(dataState.data)
//                        binding.imageDownloaderPb.visibility= View.GONE
//                    }
//                    is DataState.Error->{
//                        binding.imageDownloaderPb.visibility= View.GONE
//                        Toast.makeText(this@MainActivity,"Error",Toast.LENGTH_SHORT).show()
//                        binding.img.setImageResource(R.drawable.broken_image)
//                    }
//                    is DataState.Loading->{
//                        binding.imageDownloaderPb.visibility= View.VISIBLE
//                    }
//                }
//
//            }.launchIn(lifecycle.coroutineScope)
//        }


    }


    /**
     * - saves the bitmap to Internal storage with a name
     * - make sure the bitmap is not null
     * - get the image by it's name using the fun getImageFromInternalStorage()
     */
    suspend fun saveImageToInternalStorage(context: Context, bitmap: Bitmap, imageName:String):String{
        val dir=context.filesDir
        val file= File(dir, IMAGES_FILE)
        if (!file.exists()){
            file.mkdir()
        }
        val image: File = File(file,imageName)
        val fos= FileOutputStream(image)
        bitmap.compress(Bitmap.CompressFormat.PNG,50,fos)

        return context.filesDir.absolutePath
    }

    /**
     * - get the images saved in Internal storage by their names
     * - in case the image note found pass a drawable Resource to display instead
     * - image can be saved by fun saveImageToInternalStorage()
     */
     fun getImageFromInternalStorage(context: Context, imageName: String, errorImageDrawable:Int): Bitmap?{
        return try {
            val dir=context.filesDir
            val file= File(dir,IMAGES_FILE)
            if (!file.exists()){
                file.mkdir()
            }
            val image= File(file,imageName)
            BitmapFactory.decodeStream(FileInputStream(image))
        }catch (e:Exception){
            return  BitmapFactory.decodeResource(context.resources,
                errorImageDrawable);
        }


    }

    /**
     * -  delete the image by it's name
     * - true if deleted /false if not deleted
     */
    suspend fun deleteImage(context: Context,imageName:String):Boolean{
        var deletedSuccessfully:Boolean=false
        val dir=context.filesDir
        val file= File(dir,IMAGES_FILE)
        if (file.exists())
        {
            val image= File(file,imageName)
            if (image.exists()) {
                deletedSuccessfully = image.delete()
            }
        }

        return deletedSuccessfully

    }

}
