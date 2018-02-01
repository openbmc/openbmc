require gstreamer1.0-vaapi.inc
SRC_URI[md5sum] = "318af17f906637570b61dd7be9b5581d"
SRC_URI[sha256sum] = "03e690621594d9f9495d86c7dac8b8590b3a150462770ed070dc76f66a70de75"

SRC_URI += "file://vaapivideobufferpool-create-allocator-if-needed.patch"

DEPENDS += "gstreamer1.0 gstreamer1.0-plugins-base gstreamer1.0-plugins-bad"
