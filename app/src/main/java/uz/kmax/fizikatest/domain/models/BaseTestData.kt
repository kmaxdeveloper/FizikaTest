package uz.kmax.fizikatest.domain.models

class BaseTestData(
    var answer: String = "", var question: String = "",
    var variantA: String = "", var variantB: String = "",
    var variantC: String = "", var variantD: String = "",
    var imageOrNot : Int = 0, var imageUrl : String = ""
)