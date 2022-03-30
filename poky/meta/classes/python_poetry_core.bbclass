inherit python_pep517 python3native setuptools3-base

DEPENDS += "python3-poetry-core-native"

PEP517_BUILD_API = "poetry.core.masonry.api"
