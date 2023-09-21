package com.mostafan3ma.android.barcode11.oporations.utils

interface Mapper<Entity,Domain> {
    fun mapToEntity(domain: Domain):Entity
    fun mapFromEntity(entity: Entity):Domain
    fun mapEntityList(entityList:List<Entity>) :List<Domain>
}