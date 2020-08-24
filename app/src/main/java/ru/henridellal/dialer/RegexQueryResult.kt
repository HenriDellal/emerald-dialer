package ru.henridellal.dialer

class RegexQueryResult(@JvmField val position: Int, @JvmField val start: Int, @JvmField val end: Int) : Comparable<RegexQueryResult?> {
    @JvmField
	var numberStart = 0
    @JvmField
	var numberEnd = 0
    fun setNumberPlace(numberStart: Int, numberEnd: Int) {
        this.numberStart = numberStart
        this.numberEnd = numberEnd
    }

    @Throws(NullPointerException::class, ClassCastException::class)
    override fun compareTo(obj: RegexQueryResult?): Int {
        if (null == obj) {
            throw NullPointerException()
        }
        val result = start.compareTo(obj.start)
        return if (result != 0) result else position.compareTo(obj.position)
    }
}