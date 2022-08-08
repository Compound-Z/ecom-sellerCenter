package vn.ztech.software.ecomSeller.util

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import java.util.*
import java.util.regex.Pattern
import kotlin.math.roundToInt


const val MOB_ERROR_TEXT = "Enter valid mobile number!"
const val PASSWORD_ERROR_TEXT = "Please enter a stronger password: minimum eight characters, at least one uppercase letter, one lowercase letter and one number!"
const val RETYPE_PASSWORD_ERROR_TEXT = "Retype password must be identical!"

const val EMAIL_ERROR_TEXT = "Enter valid email address!"
const val ERR_INIT = "ERROR"
const val ERR_EMAIL = "_EMAIL"
const val ERR_MOBILE = "_MOBILE"
const val ERR_UPLOAD = "UploadErrorException"

internal fun isEmailValid(email: String): Boolean {
	val EMAIL_PATTERN = Pattern.compile(
		"\\s*[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
				"\\@" +
				"[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
				"(" +
				"\\." +
				"[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
				")+\\s*"
	)
	return if (email.isEmpty()) {
		false
	} else {
		EMAIL_PATTERN.matcher(email).matches()
	}
}

internal fun isPhoneValid(phone: String): Boolean {
	val PHONE_PATTERN = Pattern.compile("^\\s*\\d{9}\\s*\$")
	return if (phone.isEmpty()) {
		false
	} else {
		PHONE_PATTERN.matcher(phone).matches()
	}
}

internal fun isPasswordValid(password: String): Boolean {
	val PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")
	return if (password.isEmpty()) {
		false
	} else {
		PASSWORD_PATTERN.matcher(password).matches()
	}
}
internal fun isPhoneNumberValid(phoneNumber: String): Boolean {
	/**check length, if start with +84-> length12, start with 84 length11, start with 0-> length10*/
	if (phoneNumber.startsWith("0")){
		if (phoneNumber.length != 10) return false
	}
	if (phoneNumber.startsWith("+84")){
		if (phoneNumber.length != 12) return false
	}
	if (phoneNumber.startsWith("84")){
		if (phoneNumber.length != 11) return false
	}

	val PHONENUMBER_PATTERN = Pattern.compile("^(((\\+|)84)|0)(3|5|7|8|9)+([0-9]{8})$")
	return if (phoneNumber.isEmpty()) {
		false
	} else {
		Log.d("REGEX", PHONENUMBER_PATTERN.matcher(phoneNumber).matches().toString())
		PHONENUMBER_PATTERN.matcher(phoneNumber).matches()
	}
}

internal fun standardlizePhoneNumber(phoneNumber: String): String{
	if (phoneNumber.startsWith("0")){
		val removedZero = phoneNumber.trim().substring(1)
		return "+84${removedZero}"
	}
	if (phoneNumber.startsWith("+84")){
		return phoneNumber
	}
	if (phoneNumber.startsWith("84")){
		if(phoneNumber.length == 9) return "+84${phoneNumber}"
		else if(phoneNumber.length == 11) return "+${phoneNumber}"
	}

	return "+84${phoneNumber.trim()}"
}
internal fun isZipCodeValid(zipCode: String): Boolean {
	val ZIPCODE_PATTERN = Pattern.compile("^\\s*[1-9]\\d{5}\\s*\$")
	return if (zipCode.isEmpty()) {
		false
	} else {
		ZIPCODE_PATTERN.matcher(zipCode).matches()
	}
}

internal fun getRandomString(length: Int, uNum: String, endLength: Int): String {
	val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
	fun getStr(l: Int): String = (1..l).map { allowedChars.random() }.joinToString("")
	return getStr(length) + uNum + getStr(endLength)
}

internal fun getProductId(ownerId: String, proCategory: String): String {
	val uniqueId = UUID.randomUUID().toString()
	return "pro-$proCategory-$ownerId-$uniqueId"
}

internal fun getOfferPercentage(costPrice: Double, sellingPrice: Double): Int {
	if (costPrice == 0.0 || sellingPrice == 0.0 || costPrice <= sellingPrice)
		return 0
	val off = ((costPrice - sellingPrice) * 100) / costPrice
	return off.roundToInt()
}

internal fun getAddressId(userId: String): String {
	val uniqueId = UUID.randomUUID().toString()
	return "$userId-$uniqueId"
}

internal fun getFullPath(context: Context, uri: Uri): String{
	var filePath = ""
	val wholeID = DocumentsContract.getDocumentId(uri)

	// Split at colon, use second item in the array
	val id = wholeID.split(":").toTypedArray()[1]

	val column = arrayOf(MediaStore.Images.Media.DATA)


	// where id is equal to
	val sel = MediaStore.Images.Media._ID + "=?"

	val cursor: Cursor? = context.contentResolver.query(
		MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
		column, sel, arrayOf(id), null
	)

	val columnIndex: Int = cursor?.getColumnIndex(column[0])?:0

	if (cursor?.moveToFirst() == true) {
		filePath = cursor.getString(columnIndex)
	}
	cursor?.close()
	return filePath
}


