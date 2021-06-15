inherit setuptools3

do_compile_class-native() {
    :
}

do_install_class-native() {
    :
}

DEPENDS_append_class-target = " python"

inherit skeleton
inherit allarch
