GITHUB_BASE_URI ?= "https://github.com/${BPN}/${BPN}/releases/"
UPSTREAM_CHECK_URI ?= "${GITHUB_BASE_URI}"
UPSTREAM_CHECK_REGEX ?= "releases/tag/v?(?P<pver>\d+(\.\d+)+)"
