package vn.ztech.software.ecomSeller.exception

import vn.ztech.software.ecomSeller.util.CustomError

class RefreshTokenExpiredException(override var customMessage: String): CustomError(customMessage = customMessage)