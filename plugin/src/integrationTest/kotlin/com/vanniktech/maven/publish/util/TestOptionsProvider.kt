package com.vanniktech.maven.publish.util

import com.google.testing.junit.testparameterinjector.junit5.TestParameterValuesProvider

private val quickTestProperty get() = System.getProperty("quickTest")

internal class TestOptionsConfigProvider : TestParameterValuesProvider() {
  override fun provideValues(context: Context?): List<*> {
    val property = System.getProperty("testConfigMethod")
    if (property.isNotBlank()) {
      return listOf(TestOptions.Config.valueOf(property))
    }
    if (quickTestProperty.isNotBlank()) {
      return listOf(TestOptions.Config.BASE)
    }
    return TestOptions.Config.values().toList()
  }
}

internal class GradleVersionProvider : TestParameterValuesProvider() {
  override fun provideValues(context: Context?): List<*> {
    if (quickTestProperty.isNotBlank()) {
      return listOf(GradleVersion.values().last())
    }
    return GradleVersion.values().distinctBy { it.value }
  }
}

internal class AgpVersionProvider : TestParameterValuesProvider() {
  override fun provideValues(context: Context?): List<*> {
    if (quickTestProperty.isNotBlank()) {
      return listOf(AgpVersion.values().last())
    }
    return AgpVersion.values().distinctBy { it.value }
  }
}

internal class KotlinVersionProvider : TestParameterValuesProvider() {
  override fun provideValues(context: Context?): List<*> {
    if (quickTestProperty.isNotBlank()) {
      return listOf(KotlinVersion.values().last())
    }
    return KotlinVersion.values().distinctBy { it.value }
  }
}

internal class GradlePluginPublishVersionProvider : TestParameterValuesProvider() {
  override fun provideValues(context: Context?): List<*> {
    if (quickTestProperty.isNotBlank()) {
      return listOf(GradlePluginPublish.values().last())
    }
    return GradlePluginPublish.values().distinctBy { it.version }
  }
}
