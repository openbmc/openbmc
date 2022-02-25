inherit pip_install_wheel python3native python3-dir

DEPENDS += "python3 python3-flit-core-native python3-pip-native"

do_configure () {
    mkdir -p ${S}/dist
    cat > ${S}/build-it.py << EOF
from flit_core import buildapi
buildapi.build_wheel('./dist')
EOF
}

do_compile () {
    ${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} ${S}/build-it.py
}

