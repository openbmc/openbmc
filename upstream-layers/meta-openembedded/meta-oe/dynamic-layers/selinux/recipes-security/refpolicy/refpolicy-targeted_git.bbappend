FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"

SRC_URI:append = " \
        file://0001-Added-sepolicy-for-adb-service.patch \
        "
