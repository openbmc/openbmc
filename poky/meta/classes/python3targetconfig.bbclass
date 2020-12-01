inherit python3native

EXTRA_PYTHON_DEPENDS ?= ""
EXTRA_PYTHON_DEPENDS_class-target = "python3"
DEPENDS_append = " ${EXTRA_PYTHON_DEPENDS}"

do_configure_prepend_class-target() {
        export _PYTHON_SYSCONFIGDATA_NAME="_sysconfigdata"
}

do_compile_prepend_class-target() {
        export _PYTHON_SYSCONFIGDATA_NAME="_sysconfigdata"
}

do_install_prepend_class-target() {
        export _PYTHON_SYSCONFIGDATA_NAME="_sysconfigdata"
}
