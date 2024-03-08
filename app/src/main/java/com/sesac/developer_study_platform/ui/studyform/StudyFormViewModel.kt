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

    fun setImageUri(uri: Uri) {
        _imageUriEvent.value = Event(uri)
        _isSelectedImage.value = Event(true)
    }
}