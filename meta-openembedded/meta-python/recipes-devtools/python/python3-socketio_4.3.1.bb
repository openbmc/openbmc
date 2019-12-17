inherit setuptools3

PACKAGECONFIG ?= "asyncio_client client"
require python-socketio.inc
