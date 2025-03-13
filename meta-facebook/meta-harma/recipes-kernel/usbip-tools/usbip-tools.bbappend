# hwdata provides important hardware-related data files, such as vendor and 
# product IDs for USB devices. This is essential for tools like usbip to 
# correctly identify connected USB devices and display meaningful information 
# (e.g., vendor/product names) instead of showing "unknown vendor".
RDEPENDS:${PN} += " hwdata"