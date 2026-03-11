.. SPDX-License-Identifier: CC-BY-2.5

=================
Library Functions
=================

|

This chapter lists common library functions available under the ``lib/``
directory in BitBake.

These functions can be used in recipes or configuration files with
:ref:`inline-Python <bitbake-user-manual/bitbake-user-manual-metadata:Inline
Python Variable Expansion>` or :ref:`Python
<bitbake-user-manual/bitbake-user-manual-metadata:BitBake-Style Python
Functions>` functions.

Logging utilities
=================

Different logging utilities can be used from Python code in recipes or
configuration files.

The strings passed below can be formatted with ``str.format()``, for example::

   bb.warn("Houston, we have a %s", "bit of a problem")

Formatted string can also be used directly::

   bb.error("%s, we have a %s" % ("Houston", "big problem"))

Python f-strings may also be used::

   h = "Houston"
   bb.fatal(f"{h}, we have a critical problem")

.. automodule:: bb
   :members:
      debug,
      error,
      erroronce,
      fatal,
      note,
      plain,
      verbnote,
      warn,
      warnonce,

``bb.utils``
============

.. automodule:: bb.utils
   :members:
   :exclude-members:
      LogCatcher,
      PrCtlError,
      VersionStringException,
      better_compile,
      better_exec,
