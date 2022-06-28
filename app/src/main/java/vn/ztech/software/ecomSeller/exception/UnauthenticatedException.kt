package vn.ztech.software.ecomSeller.exception

import vn.ztech.software.ecomSeller.util.CustomError

class UnauthenticatedException(override var customMessage: String = "Wrong phone number or password") : CustomError(customMessage = customMessage)