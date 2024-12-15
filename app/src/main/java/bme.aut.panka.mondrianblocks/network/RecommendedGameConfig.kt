package bme.aut.panka.mondrianblocks.network

data class StackTraceElement(
    val classLoaderName: String?,
    val moduleName: String?,
    val moduleVersion: String?,
    val methodName: String?,
    val fileName: String?,
    val lineNumber: Int?,
    val nativeMethod: Boolean?,
    val className: String?
)

data class ExceptionDetails(
    val stackTrace: List<StackTraceElement>?,
    val message: String?,
    val localizedMessage: String?
)

data class CancellationException(
    val cause: ExceptionDetails?,
    val stackTrace: List<StackTraceElement>?,
    val message: String?,
    val suppressed: List<ExceptionDetails>?,
    val localizedMessage: String?
)

data class OnEvent(
    val clauseObject: Any?,
    val regFunc: Any?,
    val processResFunc: Any?,
    val onCancellationConstructor: Any?
)

data class Parent(
    val parent: String?,
    val isActive: Boolean?,
    val isCancelled: Boolean?,
    val children: Any?,
    val isCompleted: Boolean?,
    val cancellationException: CancellationException?,
    val onJoin: OnEvent?,
    val key: Any?
)

data class RecommendedGameConfig(
    val completed: Map<String, Any>?,
    val onAwait: OnEvent?,
    val completionExceptionOrNull: ExceptionDetails?,
    val parent: Parent?,
    val isActive: Boolean?,
    val isCancelled: Boolean?,
    val children: Any?,
    val isCompleted: Boolean?,
    val cancellationException: CancellationException?,
    val onJoin: OnEvent?,
    val key: Any?
)
