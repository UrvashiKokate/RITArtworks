package com.urvashikokate.ritartworks

class UserModel(

    var email: String="",
    var name: String="",
    var password: String = "",
    var phone: String=""
) {


    constructor() : this(" ", "","","")
}
