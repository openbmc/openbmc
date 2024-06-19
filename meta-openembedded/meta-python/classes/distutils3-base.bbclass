DEPENDS:append:class-target = " python3-native python3"
DEPENDS:append:class-nativesdk = " python3-native python3"
RDEPENDS:${PN} += "${@['', 'python3-core']['${CLASSOVERRIDE}' == 'class-target']}"

inherit distutils-common-base python3native python3targetconfig

python __anonymous() {
    bb.warn("distutils3-base.bbclass is deprecated, please use setuptools3-base.bbclass instead")
}
