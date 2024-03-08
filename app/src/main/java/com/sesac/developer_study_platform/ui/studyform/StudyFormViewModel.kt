import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
class StudyFormViewModel : ViewModel() {

    private val _imageUriEvent: MutableLiveData<Event<Uri>> = MutableLiveData()
    val imageUriEvent: LiveData<Event<Uri>> = _imageUriEvent

    private val _isSelectedImage: MutableLiveData<Event<Boolean>> = MutableLiveData(Event(false))
    val isSelectedImage: LiveData<Event<Boolean>> = _isSelectedImage

    private val _uploadImageEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val uploadImageEvent: LiveData<Event<String>> = _uploadImageEvent

    private val _selectedCategoryEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val selectedCategory: LiveData<Event<String>> = _selectedCategoryEvent

    private val _nameEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val nameEvent: LiveData<Event<String>> = _nameEvent

    private val _contentEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val contentEvent: LiveData<Event<String>> = _contentEvent

    private val _isNameValidate: MutableLiveData<Event<Boolean>> = MutableLiveData(Event(false))
    val isNameValidate: LiveData<Event<Boolean>> = _isNameValidate

    private val _isContentValidate: MutableLiveData<Event<Boolean>> = MutableLiveData(Event(false))
    val isContentValidate: LiveData<Event<Boolean>> = _isContentValidate
    fun setImageUri(uri: Uri) {
        _imageUriEvent.value = Event(uri)
        _isSelectedImage.value = Event(true)
    }

    fun selectCategory(category: String) {
        _selectedCategoryEvent.value = Event(category)
    }

    fun validateName(name: String) {
        if (name.length == 20) {
            _isNameValidate.value = Event(true)
        } else {
            _nameEvent.value = Event(name)
        }
    }

    fun validateContent(content: String) {
        if (content.length == 150) {
            _isContentValidate.value = Event(true)
        } else {
            _contentEvent.value = Event(content)
        }
    }
}