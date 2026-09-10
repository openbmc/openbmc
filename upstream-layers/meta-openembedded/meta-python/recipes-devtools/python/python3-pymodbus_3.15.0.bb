SUMMARY = "A fully featured modbus protocol stack in python"
HOMEPAGE = "https://github.com/riptideio/pymodbus/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=12a490c6cffa2e76a6df8aa1fa29e183"

SRC_URI[sha256sum] = "4c5f715128bfeba59f4c9fb3542b0c32a8afd4b90081111e39c12f7af0c89aae"

inherit pypi python_setuptools_build_meta

PACKAGECONFIG ??= ""
PACKAGECONFIG[repl] = ",,,python3-aiohttp python3-click python3-prompt-toolkit python3-pygments python3-pyserial-asyncio"
PACKAGECONFIG[asyncio] = ",,,python3-pyserial-asyncio"
PACKAGECONFIG[tornado] = ",,,python3-tornado"
PACKAGECONFIG[twisted] = ",,,python3-twisted-conch"
PACKAGECONFIG[redis] = ",,,python3-redis"
PACKAGECONFIG[sql] = ",,,python3-sqlalchemy"

RDEPENDS:${PN} += " \
    python3-asyncio \
    python3-core \
    python3-io \
    python3-json \
    python3-logging \
    python3-math \
    python3-netserver \
"

RDEPENDS:${PN} += " \
    python3-pyserial \
    python3-six \
"
