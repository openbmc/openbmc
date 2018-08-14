SUMMARY = "Dependency graphing for Python"
DESCRIPTION = " Generate dependency graphs from Python code. This \
dependency tracker package has a few distinguishing characteristics \
\
    * It uses the AST to parse the Python files. This is very reliable, \
      it always runs.\
    * No module is loaded. Loading modules to figure out dependencies is \
      almost always problem, because a lot of codebases run initialization \
      code in the global namespace, which often requires additional setup. \
      Snakefood is guaranteed not to have this problem (it just runs, no \
      matter what).\
    * It works on a set of files, i.e. you do not have to specify a single \
      script, you can select a directory (package or else) or a set of files.\
      It finds all the Python files recursively automatically.\
    * Automatic/no configuration: your PYTHONPATH is automatically adjusted \
      to include the required package roots. It figures out the paths that \
      are required from the files/directories given as input. You should not \
      have to setup ANYTHING.\
    * It does not have to automatically 'follow' dependencies between modules,\
      i.e. by default it only considers the files and directories you specify \
      on the command-line and their immediate dependencies. It also has an \
      option to automatically include only the dependencies within the \
      packages of the files you specify.\
    * It follows the UNIX philosophy of small programs that do one thing well:\
      it consists of a few simple programs whose outputs you combine via \
      pipes. Graphing dependencies always requires the user to filter and \
      cluster the filenames, so this is appropriate. You can combine it with \
      your favourite tools, grep, sed, etc.\
\
A problem with dependency trackers that run code is that they are unreliable, \
due to the dynamic nature of Python (the presence of imports within function \
calls and __import__ hooks makes it almost impossible to always do the right \
thing). This script aims at being right 99% of the time, and we think that \
given the trade-offs, 99% is good enough for 99% of the uses.\
"
AUTHOR = "Martin Blais <blais@furius.ca>"
HOMEPAGE = "http://furius.ca/snakefood"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

SRC_URI[md5sum] = "56c88667a33d8909b0aabf2ab6903bdf"
SRC_URI[sha256sum] = "295784668032254e7391ca99ba7060edd3ae4eca1a330ac11627b18ab5923b77"

inherit pypi setuptools

RDEPENDS_${PN} = " python-logging python-compiler python-shell"
# the above modules do not have a -native counterpart
RDEPENDS_${PN}_class-native = ""

BBCLASSEXTEND = "native"

