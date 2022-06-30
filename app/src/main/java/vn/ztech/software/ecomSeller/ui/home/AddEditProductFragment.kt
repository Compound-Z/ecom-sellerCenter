package vn.ztech.software.ecomSeller.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import vn.ztech.software.ecomSeller.R
import vn.ztech.software.ecomSeller.api.request.CreateProductRequest
import vn.ztech.software.ecomSeller.common.StoreDataStatus
import vn.ztech.software.ecomSeller.databinding.FragmentAddEditAddressBinding
import vn.ztech.software.ecomSeller.databinding.FragmentAddEditProductBinding
import vn.ztech.software.ecomSeller.model.Category
import vn.ztech.software.ecomSeller.model.Country
import vn.ztech.software.ecomSeller.model.Product
import vn.ztech.software.ecomSeller.ui.AddCategoryViewErrors
import vn.ztech.software.ecomSeller.ui.AddProductViewErrors
import vn.ztech.software.ecomSeller.ui.BaseFragment2
import vn.ztech.software.ecomSeller.ui.category.AddCategoryImagesAdapter
import vn.ztech.software.ecomSeller.ui.category.CategoryViewModel
import vn.ztech.software.ecomSeller.util.getFullPath
import java.io.File


class AddEditProductFragment : BaseFragment2<FragmentAddEditProductBinding>() {
    private val viewModel: HomeViewModel by sharedViewModel()
    //todo: Bug_Risk: shared viewModel here may contains bugs for features in future, give this a check if any weird bug shows up
    private val categoryViewModel: CategoryViewModel by sharedViewModel()
    private lateinit var spinnerCategoryAdapter: ArrayAdapter<String>
    private lateinit var spinnerOriginAdapter: ArrayAdapter<String>
    private var imgList = mutableListOf<Uri>()
    private lateinit var adapter: AddCategoryImagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.takeIf { it.containsKey("product") }?.let {
            viewModel.currentSelectedProduct.value = arguments?.getParcelable<Product>("product")
            /**do some initial setup here*/

            return
        }
        /**if product argument does not exist*/
        if (categoryViewModel.originalCategories.value == null){
            categoryViewModel.getCategories()
        }
        if (viewModel.origins.value == null){
            viewModel.getOrigins()
        }
    }
    override fun setUpViews() {
        super.setUpViews()
        /**a common setup for any case, adding or editing*/
        binding.addProAppBar.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
        binding.etProductName.onFocusChangeListener = focusChangeListener
        binding.etSKU.onFocusChangeListener = focusChangeListener
        binding.etPrice.onFocusChangeListener = focusChangeListener
        binding.etWeight.onFocusChangeListener = focusChangeListener
        binding.etStockNumber.onFocusChangeListener = focusChangeListener
        binding.etDescription.onFocusChangeListener = focusChangeListener
        binding.etBrand.onFocusChangeListener = focusChangeListener
        adapter = AddCategoryImagesAdapter(requireContext(), imgList)
        binding.recyclerViewAddedImage.adapter = adapter
        binding.btAddProductImg.setOnClickListener {
            getImages.launch("image/*")
        }

        binding.btAddEditProduct.setOnClickListener {
            onAddEditProduct()
        }

        if (viewModel.currentSelectedProduct.value == null){
            /**setup views for addProduct feature*/
            binding.addProAppBar.topAppBar.title = "Add new product"
            binding.btAddEditProduct.text = "Add new product"
        }else{
            /**setup views for editProduct feature*/
            binding.addProAppBar.topAppBar.title = "Update product"
            binding.btAddEditProduct.text = "Update product"
        }
    }


    override fun observeView() {
        super.observeView()

        categoryViewModel.originalCategories.observe(viewLifecycleOwner){
            it?.let {
                populateCategoriesToSpinner(binding.spinnerCategory, it, object : AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        if(binding.spinnerCategory.getItemAtPosition(p2).toString().isNotEmpty()){
                            categoryViewModel.currentSelectedCategory.value = categoryViewModel.originalCategories.value?.get(p2-1) /**p2-1 because the category list is added one empty item at it's head*/
                        }else{
                            categoryViewModel.currentSelectedCategory.value = null
                        }
                    }
                    override fun onNothingSelected(p0: AdapterView<*>?) {
                    }

                })
            }
        }
        viewModel.origins.observe(viewLifecycleOwner){
            it?.let {
                populateOriginsToSpinner(binding.spinnerOrigin, it, object : AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        if(binding.spinnerOrigin.getItemAtPosition(p2).toString().isNotEmpty()){
                            viewModel.currentSelectedOrigin.value = viewModel.origins.value?.get(p2-1) /**p2-1 because the category list is added one empty item at it's head*/
                        }else{
                            viewModel.currentSelectedOrigin.value = null
                        }
                    }
                    override fun onNothingSelected(p0: AdapterView<*>?) {
                    }

                })
            }
        }


        viewModel.storeDataStatus.observe(viewLifecycleOwner) { status ->
            if(status == StoreDataStatus.LOADING) {
                binding.loaderLayout.loaderFrameLayout.visibility = View.VISIBLE
                binding.loaderLayout.circularLoader.showAnimationBehavior
            }else{
                binding.loaderLayout.circularLoader.hideAnimationBehavior
                binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
            }
        }
        categoryViewModel.storeDataStatus.observe(viewLifecycleOwner) { status ->
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
                /**if this is an add action*/
                if(viewModel.currentSelectedProduct.value == null){
                    viewModel.currentProductInput.value?.product?.imageUrl = it.url
                    viewModel.currentProductInput.value?.productDetail?.imageUrls = listOf(it.url)
                    createNewProduct(viewModel.currentProductInput.value)
                }else{
//                    updateCategory(
//                        viewModel.currentSelectedCategory.value?._id,
//                        binding.etCategoryName.text.toString(),
//                        it.url
//                    )
                }
            }
        }
        viewModel.createdProduct.observe(viewLifecycleOwner){
            it?.let {
               toastCenter("Created product ${it.name} successfully!")
            }
        }
        viewModel.error.observe(viewLifecycleOwner){
            it ?: return@observe
            handleError(it)
        }
        viewModel.errorUI.observe(viewLifecycleOwner){
            it ?: return@observe
            if(it == AddProductViewErrors.NONE){
                //call api add product
                binding.tvError.visibility = View.GONE
                uploadImage()
            }else{
                modifyErrors(it)
            }
        }
        categoryViewModel.error.observe(viewLifecycleOwner){
            it ?: return@observe
            handleError(it)
        }
    }

    private fun createNewProduct(createProductRequest: CreateProductRequest?) {
        viewModel.createNewProduct(createProductRequest)
    }

    private fun onAddEditProduct() {
        viewModel.checkInputData(
            binding.etProductName.text.toString(),
            binding.etSKU.text.toString(),
            binding.etPrice.text.toString(),
            imgList,
            categoryViewModel.currentSelectedCategory.value?.name?:"",
            binding.etWeight.text.toString(),
            binding.etStockNumber.text.toString(),
            binding.etDescription.text.toString(),
            binding.etUnit.text.toString(),
            binding.etBrand.text.toString(),
            viewModel.currentSelectedOrigin.value?.name?:""
        )
    }
    private fun modifyErrors(err: AddProductViewErrors) {
        binding.tvError.visibility = View.GONE
        when (err) {
            AddProductViewErrors.EMPTY -> {
                binding.tvError.visibility = View.VISIBLE
                binding.tvError.text = getString(R.string.add_category_error_string)
                Toast.makeText(requireContext(), "Please fill all the details and add an image!", Toast.LENGTH_LONG).apply {
                    setGravity(Gravity.CENTER, 0, 0)
                }.show()
            }
            AddProductViewErrors.NAME->{
                binding.etProductName.error = "Name  must be 5-100"
                toastCenter("Name  must be 5-100")
            }
            AddProductViewErrors.PRICE->{
                binding.etPrice.error = "Price must be > 0"
                toastCenter("Price must be > 0")

            }
            AddProductViewErrors.WEIGHT->{
                binding.etWeight.error = "Weight must be > 0"
                toastCenter("Weight must be > 0")

            }
            AddProductViewErrors.STOCK->{
                binding.etStockNumber.error = "Stock must be > 0"
                toastCenter("Stock must be > 0")

            }
            AddProductViewErrors.DES->{
                binding.etDescription.error = "Length of description must be 50-2500"
                toastCenter("Length of description must be 50-2500")
            }
            else->{}
        }
    }
    private fun populateCategoriesToSpinner(spinnerCategory: Spinner, categories: MutableList<Category>, onItemSelectedListener: AdapterView.OnItemSelectedListener) {
        val listCategoryName = categories.map { it.name }.toMutableList()
        listCategoryName.add(0,"") /**add an empty item at the head of spinner so that it appear empty when the spinner is created instead of the first item*/
        spinnerCategoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listCategoryName)
        setupAdapterForSpinner(spinnerCategory, spinnerCategoryAdapter, onItemSelectedListener)
    }
    private fun populateOriginsToSpinner(spinnerOrigin: Spinner, origins: List<Country>, onItemSelectedListener: AdapterView.OnItemSelectedListener) {
        val listOrigins = origins.map { it.name }.toMutableList()
        listOrigins.add(0,"")
        spinnerOriginAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listOrigins)
        setupAdapterForSpinner(spinnerOrigin, spinnerOriginAdapter, onItemSelectedListener)
    }
    private fun setupAdapterForSpinner(spinner: Spinner, spinnerAdapter: ArrayAdapter<String>, onItemSelectedListener: AdapterView.OnItemSelectedListener) {
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        spinner.adapter = spinnerAdapter
        spinner.onItemSelectedListener = onItemSelectedListener
        spinner.setSelection(0)
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
                requestPermissionLauncher.launch(
                    Manifest.permission.READ_EXTERNAL_STORAGE)
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
            result?.let {
                imgList.clear()
                imgList.add(result)
                adapter.data = imgList
                binding.recyclerViewAddedImage.adapter?.notifyDataSetChanged()
            }
        }

    override fun setViewBinding(): FragmentAddEditProductBinding {
        return FragmentAddEditProductBinding.inflate(layoutInflater)
    }

    override fun onStop() {
        super.onStop()
        binding.etProductName.onFocusChangeListener = null
        binding.etSKU.onFocusChangeListener = null
        binding.etPrice.onFocusChangeListener = null
        binding.etWeight.onFocusChangeListener = null
        binding.etStockNumber.onFocusChangeListener = null
        binding.etDescription.onFocusChangeListener = null
        binding.etBrand.onFocusChangeListener = null
        viewModel.clearErrors()
        categoryViewModel.clearErrors()
    }

}