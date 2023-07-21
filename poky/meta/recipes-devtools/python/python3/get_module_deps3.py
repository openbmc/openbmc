# This script is launched on separate task for each python module
# It checks for dependencies for that specific module and prints 
# them out, the output of this execution will have all dependencies
# for a specific module, which will be parsed an dealt on create_manifest.py
#
# Author: Alejandro Enedino Hernandez Samaniego <alejandro at enedino dot org>


import sys
import os

# We can get a log per module, for all the dependencies that were found, but its messy.
if '-d' in sys.argv:
    debug = True
else:
    debug = False

# We can get a list of the modules which are currently required to run python
# so we run python-core and get its modules, we then import what we need
# and check what modules are currently running, if we substract them from the
# modules we had initially, we get the dependencies for the module we imported.

# We use importlib to achieve this, so we also need to know what modules importlib needs
import importlib

core_deps = set(sys.modules)

def fix_path(dep_path):
    import os
    # We DONT want the path on our HOST system
    pivot = 'recipe-sysroot-native'
    dep_path = dep_path[dep_path.find(pivot)+len(pivot):]

    if '/usr/bin' in dep_path:
        dep_path = dep_path.replace('/usr/bin','${bindir}')

    # Handle multilib, is there a better way?
    if '/usr/lib32' in dep_path:
        dep_path = dep_path.replace('/usr/lib32','${libdir}')
    if '/usr/lib64' in dep_path:
        dep_path = dep_path.replace('/usr/lib64','${libdir}')
    if '/usr/lib' in dep_path:
        dep_path = dep_path.replace('/usr/lib','${libdir}')
    if '/usr/include' in dep_path:
        dep_path = dep_path.replace('/usr/include','${includedir}')
    if '__init__.' in dep_path:
        dep_path =  os.path.split(dep_path)[0]
    return dep_path


# Module to import was passed as an argument
current_module =  str(sys.argv[1]).rstrip()
if debug == True:
    log = open('temp/log_%s' % current_module.strip('.*'),'w')
    log.write('Module %s generated the following dependencies:\n' % current_module)
try:
    m = importlib.import_module(current_module)
    # handle python packages which may not include all modules in the __init__
    if hasattr(m, '__file__') and os.path.basename(m.__file__) == "__init__.py":
        modulepath = os.path.dirname(m.__file__)
        for i in os.listdir(modulepath):
            if i.startswith("_") or not(i.endswith(".py")):
                continue
            submodule = "{}.{}".format(current_module, i[:-3])
            try:
                importlib.import_module(submodule)
            except:
                pass # ignore all import or other exceptions raised during import
except ImportError as e:
    if debug == True:
        log.write('Module was not found\n')
    pass


# Get current module dependencies, dif will contain a list of specific deps for this module
module_deps = set(sys.modules)

# We handle the core package (1st pass on create_manifest.py) as a special case
if current_module == 'python-core-package':
    dif = core_deps
else:
    # We know this is not the core package, so there must be a difference.
    dif = module_deps-core_deps


# Check where each dependency came from
for item in dif:
    # Main module returns script filename, __main matches mp_main__ as well
    if 'main__' in item:
        continue

    dep_path = ''
    try:
        if debug == True:
            log.write('\nCalling: sys.modules[' + '%s' % item + '].__file__\n')
        dep_path = sys.modules['%s' % item].__file__
    except AttributeError as e:
        # Deals with thread (builtin module) not having __file__ attribute
        if debug == True:
            log.write(item + ' ')
            log.write(str(e))
            log.write('\n')
        pass
    except NameError as e:
        # Deals with NameError: name 'dep_path' is not defined
        # because module is not found (wasn't compiled?), e.g. bddsm
        if debug == True:
            log.write(item+' ') 
            log.write(str(e))                                              
        pass

    if dep_path == '':
        continue
    if debug == True:
        log.write('Dependency path found:\n%s\n' % dep_path)

    # Site-customize is a special case since we (OpenEmbedded) put it there manually
    if 'sitecustomize' in dep_path:
        dep_path = '${libdir}/python${PYTHON_MAJMIN}/sitecustomize.py'
        # Prints out result, which is what will be used by create_manifest
        print (dep_path)
        continue

    dep_path = fix_path(dep_path)

    import sysconfig
    soabi = sysconfig.get_config_var('SOABI')
    # Check if its a shared library and deconstruct it
    if soabi in dep_path:
        if debug == True:
            log.write('Shared library found in %s\n' % dep_path)
        dep_path = dep_path.replace(soabi,'*')
        print (dep_path)
        continue
    if "_sysconfigdata" in dep_path:
        dep_path = dep_path.replace(sysconfig._get_sysconfigdata_name(), "_sysconfigdata*")

    if debug == True:
        log.write(dep_path+'\n')
    # Prints out result, which is what will be used by create_manifest
    print (dep_path)


    cpython_tag = sys.implementation.cache_tag
    cached = ''
    # Theres no naive way to find *.pyc files on python3
    try:
        if debug == True:
            log.write('\nCalling: sys.modules[' + '%s' % item + '].__cached__\n')
        cached = sys.modules['%s' % item].__cached__
    except AttributeError as e:
        # Deals with thread (builtin module) not having __cached__ attribute
        if debug == True:
            log.write(item + ' ')
            log.write(str(e))
            log.write('\n')
        pass
    except NameError as e:
        # Deals with NameError: name 'cached' is not defined
        if debug == True:
            log.write(item+' ') 
            log.write(str(e))                                              
        pass
    if cached is not None:
        if debug == True:
            log.write(cached + '\n')
        cached = fix_path(cached)
        cached = cached.replace(cpython_tag,'*')
        if "_sysconfigdata" in cached:
            cached = cached.replace(sysconfig._get_sysconfigdata_name(), "_sysconfigdata*")
        print (cached)

if debug == True:
    log.close()
