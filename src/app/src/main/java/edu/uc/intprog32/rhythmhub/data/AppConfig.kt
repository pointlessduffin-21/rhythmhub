package edu.uc.intprog32.rhythmhub.data

/**
 * Application configuration for environment-based behavior.
 *
 * In DEV mode:
 * - Mock data is seeded automatically
 * - Debug logging is enabled
 * - Test users are available
 *
 * In PROD mode:
 * - Only real Firestore data is used
 * - Debug features are disabled
 */
object AppConfig {

    /**
     * Current environment. Change this to switch between DEV and PROD. TODO: In production, set
     * this via BuildConfig or environment variable.
     */
    val ENV: Environment = Environment.DEV

    val isDev: Boolean
        get() = ENV == Environment.DEV
    val isProd: Boolean
        get() = ENV == Environment.PROD

    /** Mock user ID for development testing. In PROD this should never be used. */
    const val DEV_TEST_USER_ID = "dev_test_user"
    const val DEV_TEST_USERNAME = "DevTester"

    /** Enable seeding of mock arcade data in Firestore for development. */
    val shouldSeedMockData: Boolean
        get() = isDev

    /** Enable verbose logging. */
    val enableDebugLogging: Boolean
        get() = isDev
}

enum class Environment {
    DEV,
    PROD
}

