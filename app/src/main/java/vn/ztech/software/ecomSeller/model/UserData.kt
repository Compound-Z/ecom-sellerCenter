package vn.ztech.software.ecomSeller.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.android.parcel.Parcelize
import vn.ztech.software.ecomSeller.database.local.user.ObjectListTypeConvertor
import vn.ztech.software.ecomSeller.ui.UserType
import kotlin.collections.ArrayList

@Parcelize
@Entity(tableName = "users")
data class UserData(
	@PrimaryKey
	var userId: String = "",
	var name: String = "",
	var phoneNumber: String = "",
	var email: String = "",
	var password: String = "",
	var role: String = UserType.CUSTOMER.name /**note: this field is "role" in the API*/,
	var likes: List<String> = ArrayList(),
//	@TypeConverters(ObjectListTypeConvertor::class)
//	var addresses: List<Address> = ArrayList(),
	@TypeConverters(ObjectListTypeConvertor::class)
	var cart: List<CartItem> = ArrayList(),
) : Parcelable {
//	fun toHashMap(): HashMap<String, Any> {
//		return hashMapOf(
//			"userId" to userId,
//			"name" to name,
//			"email" to email,
//			"phoneNumber" to phoneNumber,
//			"password" to password,
//			"likes" to likes,
//			"addresses" to addresses.map { it.toHashMap() },
//			"role" to role
//		)
//	}
}