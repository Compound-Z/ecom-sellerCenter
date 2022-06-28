package vn.ztech.software.ecomSeller.ui.category

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import vn.ztech.software.ecomSeller.R
import vn.ztech.software.ecomSeller.ui.BaseFragment2
import kotlin.properties.Delegates
import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.ztech.software.ecomSeller.api.request.CreateCategoryRequest
import vn.ztech.software.ecomSeller.common.StoreDataStatus
import vn.ztech.software.ecomSeller.databinding.FragmentAddEditCategoryBinding
import vn.ztech.software.ecomSeller.model.Category
import vn.ztech.software.ecomSeller.ui.AddCategoryViewErrors
import vn.ztech.software.ecomSeller.util.getFullPath
import java.io.File
import java.util.*

private const val TAG = "AddProductFragment"

class AddEditCategoryFragment : BaseFragment2<FragmentAddEditCategoryBinding>() {
        // arguments
    private val viewModel: CategoryViewModel by viewModel()
    private var imgList = mutableListOf<Uri>()
    private lateinit var adapter: AddCategoryImagesAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.takeIf { it.containsKey("category") }?.let {
            viewModel.currentSelectedCategory.value = it.getParcelable<Category>("category")
            imgList.add(viewModel.currentSelectedCategory.value?.imageUrl?.toUri()!!)
        }
    }

    override fun setUpViews() {
        super.setUpViews()
        Log.d(TAG, "set views")

        if (viewModel.currentSelectedCategory.value==null) {
            binding.addProAppBar.topAppBar.title =
                "Add Category - ${viewModel.currentSelectedCategory.value?.name}"
        }else{
            binding.addProAppBar.topAppBar.title =
                "Edit Category - ${viewModel.currentSelectedCategory.value?.name}"
        }

        adapter = AddCategoryImagesAdapter(requireContext(), imgList)
        binding.recyclerViewAddImage.adapter = adapter

        binding.addProImagesBtn.setOnClickListener {
            getImages.launch("image/*")
        }

        binding.addProAppBar.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.loaderLayout.loaderFrameLayout.visibility = View.GONE


        binding.addProErrorTextView.visibility = View.GONE
        binding.etCategoryName.onFocusChangeListener = focusChangeListener
        binding.etNumOfProInCate.onFocusChangeListener = focusChangeListener

        binding.addProBtn.setOnClickListener {
            onAddProduct()
//            if (viewModel.errorStatus.value == AddCategoryViewErrors.NONE) {
//                viewModel.addProductErrors.observe(viewLifecycleOwner) { err ->
//                    if (err == AddProductErrors.NONE) {
//                        findNavController().navigate(R.id.action_addProductFragment_to_homeFragment)
//                    }
//                }
//            }
        }
    }

    override fun observeView() {
        super.observeView()
        viewModel.storeDataStatus.observe(viewLifecycleOwner) { status ->
            if(status == StoreDataStatus.LOADING) {
                binding.loaderLayout.loaderFrameLayout.visibility = View.VISIBLE
                binding.loaderLayout.circularLoader.showAnimationBehavior
            }else{
                binding.loaderLayout.circularLoader.hideAnimationBehavior
                binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
            }
        }
        viewModel.uploadedImage.observe(viewLifecycleOwner){
            it?.let {
                createNewCategory(
                    binding.etCategoryName.text.toString(), binding.etNumOfProInCate.text.toString(), it.url
                )
            }
        }
        viewModel.createdCategory.observe(viewLifecycleOwner){
            it?.let {
                Toast.makeText(requireContext(), "Created new category ${it.name}", Toast.LENGTH_LONG)
                    .apply {
                        setGravity(Gravity.CENTER, 0, 0)
                    }
                    .show()
            }
        }
        viewModel.errorUI.observe(viewLifecycleOwner){
            it?.let {
                if(it == AddCategoryViewErrors.NONE){
                    uploadImage()
                }else{
                    modifyErrors(it)
                }
            }
        }
        viewModel.error.observe(viewLifecycleOwner){
            it ?: return@observe
            handleError(it)
        }
    }

    private fun createNewCategory(etCategoryName: String, etNumOfProInCate: String, url: String) {
        viewModel.createCategory(CreateCategoryRequest(name = etCategoryName, numberOfProduct = etNumOfProInCate.toInt(), imageUrl = url))
    }

    private fun onAddProduct() {
        /**for now placing it here, to to other part later*/
        viewModel.checkInputData(
            binding.etCategoryName.text.toString(),
            binding.etNumOfProInCate.text.toString(),
            imgList
        )
    }

    private fun uploadImage() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                viewModel.uploadImage(File(getFullPath(requireContext(), imgList[0])))
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                Toast.makeText(requireContext(), "Please grant permission to upload image", Toast.LENGTH_LONG).show()
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                    Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                viewModel.uploadImage(File(getFullPath(requireContext(), imgList[0])))
            } else {
                Toast.makeText(requireContext(), "Please grant permission to upload image", Toast.LENGTH_LONG).show()
            }
        }

    private val getImages =
        registerForActivityResult(ActivityResultContracts.GetContent()) { result ->
            imgList.clear()
            imgList.add(result)
            adapter.data = imgList
            binding.recyclerViewAddImage.adapter?.notifyDataSetChanged()
        }
    private fun makeToast(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }
    private fun modifyErrors(err: AddCategoryViewErrors) {
                when (err) {
                    AddCategoryViewErrors.NONE -> binding.addProErrorTextView.visibility = View.GONE
                    AddCategoryViewErrors.EMPTY -> {
                        binding.addProErrorTextView.visibility = View.VISIBLE
                        binding.addProErrorTextView.text = getString(R.string.add_category_error_string)
                    }
                }
    }
//
//    private fun initViewModel() {
//        Log.d(TAG, "init view model, isedit = $isEdit")
//
//        viewModel.setIsEdit(isEdit)
//        if (isEdit) {
//            Log.d(TAG, "init view model, isedit = true, $productId")
//            viewModel.setProductData(productId)
//        } else {
//            Log.d(TAG, "init view model, isedit = false, $catName")
//            viewModel.setCategory(catName)
//        }
//    }
//
//    private fun setObservers() {
//        viewModel.errorStatus.observe(viewLifecycleOwner) { err ->
//            modifyErrors(err)
//        }
//        viewModel.dataStatus.observe(viewLifecycleOwner) { status ->
//            when (status) {
//                StoreDataStatus.LOADING -> {
//                    binding.loaderLayout.loaderFrameLayout.visibility = View.VISIBLE
//                    binding.loaderLayout.circularLoader.showAnimationBehavior
//                }
//                StoreDataStatus.DONE -> {
//                    binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
//                    binding.loaderLayout.circularLoader.hideAnimationBehavior
//                    fillDataInAllViews()
//                }
//                else -> {
//                    binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
//                    binding.loaderLayout.circularLoader.hideAnimationBehavior
//                    makeToast("Error getting Data, Try Again!")
//                }
//            }
//        }
//        viewModel.addProductErrors.observe(viewLifecycleOwner) { status ->
//            when (status) {
//                AddProductErrors.ADDING -> {
//                    binding.loaderLayout.loaderFrameLayout.visibility = View.VISIBLE
//                    binding.loaderLayout.circularLoader.showAnimationBehavior
//                }
//                AddProductErrors.ERR_ADD_IMG -> {
//                    setAddProductErrors(getString(R.string.add_product_error_img_upload))
//                }
//                AddProductErrors.ERR_ADD -> {
//                    setAddProductErrors(getString(R.string.add_product_insert_error))
//                }
//                AddProductErrors.NONE -> {
//                    binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
//                    binding.loaderLayout.circularLoader.hideAnimationBehavior
//                }
//            }
//        }
//    }
//
//    private fun setAddProductErrors(errText: String) {
//        binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
//        binding.loaderLayout.circularLoader.hideAnimationBehavior
//        binding.addProErrorTextView.visibility = View.VISIBLE
//        binding.addProErrorTextView.text = errText
//
//    }
//
//    private fun fillDataInAllViews() {
//        viewModel.productData.value?.let { product ->
//            Log.d(TAG, "fill data in views")
//            binding.addProAppBar.topAppBar.title = "Edit Product - ${product.name}"
//            binding.proNameEditText.setText(product.name)
//            binding.proPriceEditText.setText(product.price.toString())
//            binding.proMrpEditText.setText(product.mrp.toString())
//            binding.proDescEditText.setText(product.description)
//
//            imgList = product.images.map { it.toUri() } as MutableList<Uri>
//            val adapter = AddProductImagesAdapter(requireContext(), imgList)
//            binding.addProImagesRv.adapter = adapter
//
//            setShoeSizesChips(product.availableSizes)
//            setShoeColorsChips(product.availableColors)
//
//            binding.addProBtn.setText(R.string.edit_product_btn_text)
//        }
//
//    }
//
//   
//
//    private fun makeToast(text: String) {
//        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
//    }
    override fun setViewBinding(): FragmentAddEditCategoryBinding {
        return FragmentAddEditCategoryBinding.inflate(layoutInflater)
    }
}