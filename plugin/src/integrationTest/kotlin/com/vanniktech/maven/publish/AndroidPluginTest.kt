package com.vanniktech.maven.publish

import com.google.common.truth.TruthJUnit.assume
import com.google.testing.junit.testparameterinjector.junit5.TestParameter
import com.google.testing.junit.testparameterinjector.junit5.TestParameterInjectorTest
import com.vanniktech.maven.publish.util.ProjectResultSubject.Companion.assertThat
import com.vanniktech.maven.publish.util.AgpVersion
import com.vanniktech.maven.publish.util.AgpVersionProvider
import com.vanniktech.maven.publish.util.KotlinVersion
import com.vanniktech.maven.publish.util.KotlinVersionProvider
import com.vanniktech.maven.publish.util.TestOptions
import com.vanniktech.maven.publish.util.androidFusedLibraryProjectSpec
import com.vanniktech.maven.publish.util.androidLibraryKotlinProjectSpec
import com.vanniktech.maven.publish.util.androidLibraryProjectSpec
import com.vanniktech.maven.publish.util.assumeSupportedJdkAndGradleVersion
import com.vanniktech.maven.publish.util.fixtures
import com.vanniktech.maven.publish.util.kotlinStdlibJdk
import com.vanniktech.maven.publish.util.run

class AndroidPluginTest : BasePluginTest() {
  @TestParameterInjectorTest
  fun androidFusedLibraryProject(
    @TestParameter(valuesProvider = AgpVersionProvider::class) agpVersion: AgpVersion,
  ) {
    agpVersion.assumeSupportedJdkAndGradleVersion(gradleVersion)
    assume().that(agpVersion).isAtLeast(AgpVersion.AGP_ALPHA)

    val project = androidFusedLibraryProjectSpec(agpVersion)
    // TODO: signing plugin currently unsupported
    val result = project.run(fixtures, testProjectDir, testOptions.copy(signing = TestOptions.Signing.NO_SIGNING))

    assertThat(result).outcome().succeeded()
    assertThat(result).artifact("aar").exists()
    // TODO: signing plugin currently unsupported
    // assertThat(result).artifact("aar").isSigned()
    assertThat(result).pom().exists()
    // TODO: signing plugin currently unsupported
    // assertThat(result).pom().isSigned()
    assertThat(result).pom().matchesExpectedPom("aar")
    assertThat(result).module().exists()
    // TODO: signing plugin currently unsupported
    // assertThat(result).module().isSigned()
    assertThat(result).sourcesJar().exists()
    // TODO: signing plugin currently unsupported
    // assertThat(result).sourcesJar().isSigned()
    // TODO: actual sources jar currently unsupported
    // assertThat(result).sourcesJar().containsAllSourceFiles()
    assertThat(result).javadocJar().exists()
    // TODO: signing plugin currently unsupported
    // assertThat(result).javadocJar().isSigned()
  }

  @TestParameterInjectorTest
  fun androidLibraryKotlinProject(
    @TestParameter(valuesProvider = AgpVersionProvider::class) agpVersion: AgpVersion,
    @TestParameter(valuesProvider = KotlinVersionProvider::class) kotlinVersion: KotlinVersion,
  ) {
    agpVersion.assumeSupportedJdkAndGradleVersion(gradleVersion)
    kotlinVersion.assumeSupportedJdkAndGradleVersion(gradleVersion)

    val project = androidLibraryKotlinProjectSpec(agpVersion, kotlinVersion)
    val result = project.run(fixtures, testProjectDir, testOptions)

    assertThat(result).outcome().succeeded()
    assertThat(result).artifact("aar").exists()
    assertThat(result).artifact("aar").isSigned()
    assertThat(result).pom().exists()
    assertThat(result).pom().isSigned()
    assertThat(result).pom().matchesExpectedPom("aar", kotlinStdlibJdk(kotlinVersion))
    assertThat(result).module().exists()
    assertThat(result).module().isSigned()
    assertThat(result).sourcesJar().exists()
    assertThat(result).sourcesJar().isSigned()
    assertThat(result).sourcesJar().containsAllSourceFiles()
    assertThat(result).javadocJar().exists()
    assertThat(result).javadocJar().isSigned()
  }

  @TestParameterInjectorTest
  fun androidLibraryProject(
    @TestParameter(valuesProvider = AgpVersionProvider::class) agpVersion: AgpVersion,
  ) {
    agpVersion.assumeSupportedJdkAndGradleVersion(gradleVersion)

    val project = androidLibraryProjectSpec(agpVersion)
    val result = project.run(fixtures, testProjectDir, testOptions)

    assertThat(result).outcome().succeeded()
    assertThat(result).artifact("aar").exists()
    assertThat(result).artifact("aar").isSigned()
    assertThat(result).pom().exists()
    assertThat(result).pom().isSigned()
    assertThat(result).pom().matchesExpectedPom("aar")
    assertThat(result).module().exists()
    assertThat(result).module().isSigned()
    assertThat(result).sourcesJar().exists()
    assertThat(result).sourcesJar().isSigned()
    assertThat(result).sourcesJar().containsAllSourceFiles()
    assertThat(result).javadocJar().exists()
    assertThat(result).javadocJar().isSigned()
  }

  @TestParameterInjectorTest
  fun androidMultiVariantLibraryProject(
    @TestParameter(valuesProvider = AgpVersionProvider::class) agpVersion: AgpVersion,
  ) {
    // regular plugin does not have a way to enable multi variant config
    assume().that(config).isEqualTo(TestOptions.Config.BASE)
    agpVersion.assumeSupportedJdkAndGradleVersion(gradleVersion)

    val project = androidLibraryProjectSpec(agpVersion).copy(
      basePluginConfig = "configure(new AndroidMultiVariantLibrary(true, true))",
    )
    val result = project.run(fixtures, testProjectDir, testOptions)

    assertThat(result).outcome().succeeded()
    assertThat(result).pom().exists()
    assertThat(result).pom().isSigned()
    assertThat(result).pom().matchesExpectedPom("pom")
    assertThat(result).module().exists()
    assertThat(result).module().isSigned()

    assertThat(result).artifact("debug", "aar").exists()
    assertThat(result).artifact("debug", "aar").isSigned()
    assertThat(result).sourcesJar("debug").exists()
    assertThat(result).sourcesJar("debug").isSigned()
    assertThat(result).sourcesJar("debug").containsAllSourceFiles()
    assertThat(result).javadocJar("debug").exists()
    assertThat(result).javadocJar("debug").isSigned()

    assertThat(result).artifact("release", "aar").exists()
    assertThat(result).artifact("release", "aar").isSigned()
    assertThat(result).sourcesJar("release").exists()
    assertThat(result).sourcesJar("release").isSigned()
    assertThat(result).sourcesJar("release").containsAllSourceFiles()
    assertThat(result).javadocJar("release").exists()
    assertThat(result).javadocJar("release").isSigned()
  }
}
