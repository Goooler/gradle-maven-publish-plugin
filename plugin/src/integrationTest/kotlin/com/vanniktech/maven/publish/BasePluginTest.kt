package com.vanniktech.maven.publish

import com.vanniktech.maven.publish.TestOptions.Signing.IN_MEMORY_KEY
import java.nio.file.Path
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.Parameter
import org.junit.jupiter.params.ParameterizedClass
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

@ParameterizedClass
@MethodSource("testParametersProvider")
abstract class BasePluginTest {
  @TempDir
  lateinit var testProjectDir: Path
    private set

  @Parameter(0)
  lateinit var config: TestOptions.Config
    private set

  @Parameter(1)
  lateinit var gradleVersion: GradleVersion
    private set

  open val testOptions get() = TestOptions(config, IN_MEMORY_KEY, gradleVersion)

  @BeforeEach
  fun setup() {
    gradleVersion.assumeSupportedJdkVersion()
  }

  companion object {
    private val enableQuickTest get() = System.getProperty("quickTest").isNotBlank()

    private val testOptionsConfigProvider by lazy {
      if (enableQuickTest) {
        return@lazy listOf(TestOptions.Config.BASE)
      }
      System.getProperty("testConfigMethod").let { property ->
        if (property.isNotBlank()) {
          return@lazy listOf(TestOptions.Config.valueOf(property))
        }
      }
      return@lazy TestOptions.Config.values().toList()
    }

    private val gradleVersionProvider by lazy {
      if (enableQuickTest) {
        return@lazy listOf(GradleVersion.values().last())
      }
      return@lazy GradleVersion.values().distinctBy { it.value }
    }

    @Suppress("unused") // used by @ParameterizedClass.
    @JvmStatic
    fun testParametersProvider(): List<Arguments> = buildList {
      for (config in testOptionsConfigProvider) {
        for (gradleVersion in gradleVersionProvider) {
          add(Arguments.of(config, gradleVersion))
        }
      }
    }

    @JvmStatic
    fun agpVersionProvider(): List<AgpVersion> {
      if (enableQuickTest) {
        return listOf(AgpVersion.values().last())
      }
      return AgpVersion.values().distinctBy { it.value }
    }

    @JvmStatic
    fun kgpVersionProvider(): List<KotlinVersion> {
      if (enableQuickTest) {
        return listOf(KotlinVersion.values().last())
      }
      return KotlinVersion.values().distinctBy { it.value }
    }

    @JvmStatic
    fun gppVersionProvider(): List<GradlePluginPublish> {
      if (enableQuickTest) {
        return listOf(GradlePluginPublish.values().last())
      }
      return GradlePluginPublish.values().distinctBy { it.version }
    }
  }
}
