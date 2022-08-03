# This is needed so that multiple locations can provide the same package
from pkgutil import extend_path
__path__ = extend_path(__path__, __name__)
