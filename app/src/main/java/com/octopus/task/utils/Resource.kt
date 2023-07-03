package com.octopus.task.utils

sealed class Resource {
    class Success<T>(val data: T) : Resource()
    class Error(val message: String) : Resource()
    object Empty : Resource()
}