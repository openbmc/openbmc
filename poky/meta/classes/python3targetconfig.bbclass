inherit python3native

EXTRA_PYTHON_DEPENDS ?= ""
EXTRA_PYTHON_DEPENDS:class-target = "python3"
DEPENDS:append = " ${EXTRA_PYTHON_DEPENDS}"

do_configure:prepend:class-target() {
        export _PYTHON_SYSCONFIGDATA_NAME="_sysconfigdata"
}

do_compile:prepend:class-target() {
        export _PYTHON_SYSCONFIGDATA_NAME="_sysconfigdata"
}

do_install:prepend:class-target() {
        export _PYTHON_SYSCONFIGDATA_NAME="_sysconfigdata"
}

do_configure:prepend:class-nativesdk() {
        export _PYTHON_SYSCONFIGDATA_NAME="_sysconfigdata"
}

do_compile:prepend:class-nativesdk() {
        export _PYTHON_SYSCONFIGDATA_NAME="_sysconfigdata"
}

do_install:prepend:class-nativesdk() {
        export _PYTHON_SYSCONFIGDATA_NAME="_sysconfigdata"
}
