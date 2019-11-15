require sip.inc

DEPENDS = "python3"

inherit python3-dir python3native

PACKAGES += "python3-sip3"

FILES_python3-sip3 = "${libdir}/${PYTHON_DIR}/site-packages/"
FILES_${PN}-dbg += "${libdir}/${PYTHON_DIR}/site-packages/.debug"

