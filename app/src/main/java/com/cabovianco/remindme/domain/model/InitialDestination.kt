package com.cabovianco.remindme.domain.model

sealed interface InitialDestination {
    object Welcome : InitialDestination
    object Permission : InitialDestination
    object Main : InitialDestination
}
