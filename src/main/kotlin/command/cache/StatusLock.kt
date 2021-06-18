package command.cache

object StatusLock {

    private var lockCache = mutableListOf<String>()

    fun generateLock(userId: Long, methodName: String, methodArgs: List<String>? = null): String {
        val sb = StringBuilder()
        sb.append(userId).append(":")
        sb.append(methodName)
        if (methodArgs?.isNotEmpty() == true) {
            sb.append(":").append(methodArgs.toString())
        }
        return sb.toString()
    }

    fun lock(lockCode: String) {
        lockCache.add(lockCode)
    }

    fun freeze(lockCode: String) {
        lockCache.remove(lockCode)
    }

    fun checkLock(lockCode: String): Boolean {
        return lockCode in lockCache
    }
}