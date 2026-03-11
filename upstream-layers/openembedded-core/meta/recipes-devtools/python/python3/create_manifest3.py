# This script is used as a bitbake task to create a new python manifest
# $ bitbake python -c create_manifest
#
# Our goal is to keep python-core as small as possible and add other python
# packages only when the user needs them, hence why we split upstream python
# into several packages.
#
# In a very simplistic way what this does is: 
# Launch python and see specifically what is required for it to run at a minimum
#
# Go through the python-manifest file and launch a separate task for every single
# one of the files on each package, this task will check what was required for that
# specific module to run, these modules will be called dependencies.
# The output of such task will be a list of the modules or dependencies that were
# found for that file.
#
# Such output will be parsed by this script, we will look for each dependency on the
# manifest and if we find that another package already includes it, then we will add
# that package as an RDEPENDS to the package we are currently checking; in case we dont
# find the current dependency on any other package we will add it to the current package
# as part of FILES.
#
#
# This way we will create a new manifest from the data structure that was built during
# this process, on this new manifest each package will contain specifically only
# what it needs to run.
#
# There are some caveats which we try to deal with, such as repeated files on different
# packages, packages that include folders, wildcards, and special packages.
# Its also important to note that this method only works for python files, and shared
# libraries. Static libraries, header files and binaries need to be dealt with manually.
#
# This script differs from its python2 version mostly on how shared libraries are handled
# The manifest file for python3 has an extra field which contains the cached files for
# each package.
# Tha method to handle cached files does not work when a module includes a folder which
# itself contains the pycache folder, gladly this is almost never the case.
#
# Author: Alejandro Enedino Hernandez Samaniego <alejandro at enedino dot org>


import sys
import subprocess
import json
import os
import collections

if '-d' in sys.argv:
    debugFlag = '-d'
else:
    debugFlag = ''

# Get python version from ${PYTHON_MAJMIN}
pyversion = str(sys.argv[1])

# Hack to get native python search path (for folders), not fond of it but it works for now
pivot = 'recipe-sysroot-native'
for p in sys.path:
    if pivot in p:
        nativelibfolder = p[:p.find(pivot)+len(pivot)]

# Empty dict to hold the whole manifest
new_manifest = collections.OrderedDict()

# Check for repeated files, folders and wildcards
allfiles = []
repeated = []
wildcards = []

hasfolders = []
allfolders = []

def isFolder(value):
    value = value.replace('${PYTHON_MAJMIN}',pyversion)
    if os.path.isdir(value.replace('${libdir}',nativelibfolder+'/usr/lib')) or os.path.isdir(value.replace('${libdir}',nativelibfolder+'/usr/lib64')) or os.path.isdir(value.replace('${libdir}',nativelibfolder+'/usr/lib32')):
        return True
    else:
        return False

def isCached(item):
    if '__pycache__' in item:
        return True
    else:
        return False

def prepend_comments(comments, json_manifest):
    with open(json_manifest, 'r+') as manifest:
        json_contents = manifest.read()
        manifest.seek(0, 0)
        manifest.write(comments + json_contents)

def print_indent(msg, offset):
    for l in msg.splitlines():
        msg = ' ' * offset + l
        print(msg)


# Read existing JSON manifest
with open('python3-manifest.json') as manifest:
    # The JSON format doesn't allow comments so we hack the call to keep the comments using a marker
    manifest_str =  manifest.read()
    json_start = manifest_str.find('# EOC') + 6 # EOC + \n
    manifest.seek(0)
    comments = manifest.read(json_start)
    manifest_str = manifest.read()
    old_manifest = json.loads(manifest_str, object_pairs_hook=collections.OrderedDict)

#
# First pass to get core-package functionality, because we base everything on the fact that core is actually working
# Not exactly the same so it should not be a function
#

print_indent('Getting dependencies for package: core', 0)


# This special call gets the core dependencies and
# appends to the old manifest so it doesnt hurt what it
# currently holds.
# This way when other packages check for dependencies
# on the new core package, they will still find them
# even when checking the old_manifest

output = subprocess.check_output([sys.executable, 'get_module_deps3.py', 'python-core-package', '%s' % debugFlag]).decode('utf8')
for coredep in output.split():
    coredep = coredep.replace(pyversion,'${PYTHON_MAJMIN}')
    if isCached(coredep):
        if coredep not in old_manifest['core']['cached']:
            old_manifest['core']['cached'].append(coredep)
    else:
        if coredep not in old_manifest['core']['files']:
            old_manifest['core']['files'].append(coredep)


# The second step is to loop through the existing files contained in the core package
# according to the old manifest, identify if they are  modules, or some other type 
# of file that we cant import (directories, binaries, configs) in which case we
# can only assume they were added correctly (manually) so we ignore those and 
# pass them to the manifest directly.

for filedep in old_manifest['core']['files']:
    if isFolder(filedep):
        if isCached(filedep):
            if filedep not in old_manifest['core']['cached']:
                old_manifest['core']['cached'].append(filedep)
        else:
            if filedep not in old_manifest['core']['files']:
                old_manifest['core']['files'].append(filedep)
        continue
    if '${bindir}' in filedep:
        if filedep not in old_manifest['core']['files']:
            old_manifest['core']['files'].append(filedep)
        continue
    if filedep == '':
        continue
    if '${includedir}' in filedep:
        if filedep not in old_manifest['core']['files']:
            old_manifest['core']['files'].append(filedep)
        continue

    # Get actual module name , shouldnt be affected by libdir/bindir, etc.
    pymodule = os.path.splitext(os.path.basename(os.path.normpath(filedep)))[0]

    # We now know that were dealing with a python module, so we can import it
    # and check what its dependencies are.
    # We launch a separate task for each module for deterministic behavior.
    # Each module will only import what is necessary for it to work in specific.
    # The output of each task will contain each module's dependencies

    print_indent('Getting dependencies for module: %s' % pymodule, 2)
    output = subprocess.check_output([sys.executable, 'get_module_deps3.py', '%s' % pymodule, '%s' % debugFlag]).decode('utf8')
    print_indent('The following dependencies were found for module %s:\n' % pymodule, 4)
    print_indent(output, 6)


    for pymodule_dep in output.split():
        pymodule_dep = pymodule_dep.replace(pyversion,'${PYTHON_MAJMIN}')

        if isCached(pymodule_dep):
            if pymodule_dep not in old_manifest['core']['cached']:
                old_manifest['core']['cached'].append(pymodule_dep)
        else:
            if pymodule_dep not in old_manifest['core']['files']:
                old_manifest['core']['files'].append(pymodule_dep)


# At this point we are done with the core package.
# The old_manifest dictionary is updated only for the core package because
# all others will use this a base.


print('\n\nChecking for directories...\n')
# To improve the script speed, we check which packages contain directories
# since we will be looping through (only) those later.
for pypkg in old_manifest:
    for filedep in old_manifest[pypkg]['files']:
        if isFolder(filedep):
            print_indent('%s is a directory' % filedep, 2)
            if pypkg not in hasfolders:
                hasfolders.append(pypkg)
            if filedep not in allfolders:
                allfolders.append(filedep)



# This is the main loop that will handle each package.
# It works in a similar fashion than the step before, but
# we will now be updating a new dictionary that will eventually
# become the new manifest.
#
# The following loops though all packages in the manifest,
# through all files on each of them, and checks whether or not
# they are modules and can be imported.
# If they can be imported, then it checks for dependencies for
# each of them by launching a separate task.
# The output of that task is then parsed and the manifest is updated
# accordingly, wether it should add the module on FILES for the current package
# or if that module already belongs to another package then the current one 
# will RDEPEND on it

for pypkg in old_manifest:
    # Use an empty dict as data structure to hold data for each package and fill it up
    new_manifest[pypkg] = collections.OrderedDict()
    new_manifest[pypkg]['summary'] = old_manifest[pypkg]['summary']
    new_manifest[pypkg]['rdepends'] = []
    new_manifest[pypkg]['files'] = []
    new_manifest[pypkg]['cached'] = old_manifest[pypkg]['cached']

    # All packages should depend on core
    if pypkg != 'core':
        new_manifest[pypkg]['rdepends'].append('core')
        new_manifest[pypkg]['cached'] = []

    print('\n')
    print('--------------------------')
    print('Handling package %s' % pypkg)
    print('--------------------------')

    # Handle special cases, we assume that when they were manually added 
    # to the manifest we knew what we were doing.
    special_packages = ['misc', 'modules', 'dev', 'tests']
    if pypkg in special_packages or 'staticdev' in pypkg:
        print_indent('Passing %s package directly' % pypkg, 2)
        new_manifest[pypkg] = old_manifest[pypkg]
        continue

    for filedep in old_manifest[pypkg]['files']:
        # We already handled core on the first pass, we can ignore it now
        if pypkg == 'core':
            if filedep not in new_manifest[pypkg]['files']:
                new_manifest[pypkg]['files'].append(filedep)
            continue

        # Handle/ignore what we cant import
        if isFolder(filedep):
            new_manifest[pypkg]['files'].append(filedep)
            # Asyncio (and others) are both the package and the folder name, we should not skip those...
            path,mod = os.path.split(filedep)
            if mod != pypkg:
                continue
        if '${bindir}' in filedep:
            if filedep not in new_manifest[pypkg]['files']:
                new_manifest[pypkg]['files'].append(filedep)
            continue
        if filedep == '':
            continue
        if '${includedir}' in filedep:
            if filedep not in new_manifest[pypkg]['files']:
                new_manifest[pypkg]['files'].append(filedep)
            continue

        # Get actual module name , shouldnt be affected by libdir/bindir, etc.
        # We need to check if the imported module comes from another (e.g. sqlite3.dump)
        path, pymodule = os.path.split(filedep)
        path = os.path.basename(path)
        pymodule = os.path.splitext(os.path.basename(pymodule))[0]

        # If this condition is met, it means we need to import it from another module
        # or its the folder itself (e.g. unittest)
        if path == pypkg:
            if pymodule:
                pymodule = path + '.' + pymodule
            else:
                pymodule = path



        # We now know that were dealing with a python module, so we can import it
        # and check what its dependencies are.
        # We launch a separate task for each module for deterministic behavior.
        # Each module will only import what is necessary for it to work in specific.
        # The output of each task will contain each module's dependencies

        print_indent('\nGetting dependencies for module: %s' % pymodule, 2)
        output = subprocess.check_output([sys.executable, 'get_module_deps3.py', '%s' % pymodule, '%s' % debugFlag]).decode('utf8')
        print_indent('The following dependencies were found for module %s:\n' % pymodule, 4)
        print_indent(output, 6)

        reportFILES = []
        reportRDEPS = []

        for pymodule_dep in output.split():

            # Warning: This first part is ugly
            # One of the dependencies that was found, could be inside of one of the folders included by another package
            # We need to check if this happens so we can add the package containing the folder as an rdependency
            # e.g. Folder encodings contained in codecs
            # This would be solved if no packages included any folders

            # This can be done in two ways:
            # 1 - We assume that if we take out the filename from the path we would get
            #   the folder string, then we would check if folder string is in the list of folders
            #   This would not work if a package contains a folder which contains another folder
            #   e.g. path/folder1/folder2/filename  folder_string= path/folder1/folder2
            #   folder_string would not match any value contained in the list of folders
            #
            # 2 - We do it the other way around, checking if the folder is contained in the path
            #   e.g. path/folder1/folder2/filename  folder_string= path/folder1/folder2
            #   is folder_string inside path/folder1/folder2/filename?, 
            #   Yes, it works, but we waste a couple of milliseconds.

            pymodule_dep = pymodule_dep.replace(pyversion,'${PYTHON_MAJMIN}')
            inFolders = False
            for folder in allfolders:
                # The module could have a directory named after it, e.g. xml, if we take out the filename from the path
                # we'll end up with ${libdir}, and we want ${libdir}/xml
                if isFolder(pymodule_dep):
                    check_path = pymodule_dep
                else:
                    check_path = os.path.dirname(pymodule_dep)
                if folder in check_path :
                    inFolders = True # Did we find a folder?
                    folderFound = False # Second flag to break inner for
                    # Loop only through packages which contain folders
                    for pypkg_with_folder in hasfolders:
                        if (folderFound == False):
                            # print('Checking folder %s on package %s' % (pymodule_dep,pypkg_with_folder))
                            for folder_dep in old_manifest[pypkg_with_folder]['files'] or folder_dep in old_manifest[pypkg_with_folder]['cached']:
                                if folder_dep == folder:
                                    print ('%s directory found in %s' % (folder, pypkg_with_folder))
                                    folderFound = True
                                    if pypkg_with_folder not in new_manifest[pypkg]['rdepends'] and pypkg_with_folder != pypkg:
                                        new_manifest[pypkg]['rdepends'].append(pypkg_with_folder)
                        else:
                            break

            # A folder was found so we're done with this item, we can go on
            if inFolders:
                continue



            # No directories beyond this point
            # We might already have this module on the dictionary since it could depend on a (previously checked) module
            if pymodule_dep not in new_manifest[pypkg]['files'] and pymodule_dep not in new_manifest[pypkg]['cached']:
                # Handle core as a special package, we already did it so we pass it to NEW data structure directly
                if pypkg == 'core':
                    print('Adding %s to %s FILES' % (pymodule_dep, pypkg))
                    if pymodule_dep.endswith('*'):
                        wildcards.append(pymodule_dep)
                    if isCached(pymodule_dep):
                        new_manifest[pypkg]['cached'].append(pymodule_dep)
                    else:
                        new_manifest[pypkg]['files'].append(pymodule_dep)

                    # Check for repeated files
                    if pymodule_dep not in allfiles:
                        allfiles.append(pymodule_dep)
                    else:
                        if pymodule_dep not in repeated:
                            repeated.append(pymodule_dep)
                else:


                    # Last step: Figure out if we this belongs to FILES or RDEPENDS
                    # We check if this module is already contained on another package, so we add that one
                    # as an RDEPENDS, or if its not, it means it should be contained on the current
                    # package, and we should add it to FILES
                    for possible_rdep in old_manifest:
                        # Debug
                        # print('Checking %s ' % pymodule_dep + ' in %s' % possible_rdep)
                        if pymodule_dep in old_manifest[possible_rdep]['files'] or pymodule_dep in old_manifest[possible_rdep]['cached']:
                            # Since were nesting, we need to check its not the same pypkg
                            if(possible_rdep != pypkg):
                                if possible_rdep not in new_manifest[pypkg]['rdepends']:
                                    # Add it to the new manifest data struct as RDEPENDS since it contains something this module needs
                                    reportRDEPS.append('Adding %s to %s RDEPENDS, because it contains %s\n' % (possible_rdep, pypkg, pymodule_dep))
                                    new_manifest[pypkg]['rdepends'].append(possible_rdep)
                                break
                    else:

                      # Since this module wasnt found on another package, it is not an RDEP,
                      # so we add it to FILES for this package.
                      # A module shouldn't contain itself (${libdir}/python3/sqlite3 shouldnt be on sqlite3 files)
                      if os.path.basename(pymodule_dep) != pypkg:
                        reportFILES.append(('Adding %s to %s FILES\n' % (pymodule_dep, pypkg)))
                        if isCached(pymodule_dep):
                            new_manifest[pypkg]['cached'].append(pymodule_dep)
                        else:
                            new_manifest[pypkg]['files'].append(pymodule_dep)
                        if pymodule_dep.endswith('*'):
                            wildcards.append(pymodule_dep)
                        if pymodule_dep not in allfiles:
                            allfiles.append(pymodule_dep)
                        else:
                            if pymodule_dep not in repeated:
                                repeated.append(pymodule_dep)

        print('\n')
        print('#################################')
        print('Summary for module %s' % pymodule)
        print('FILES found for module %s:' % pymodule)
        print(''.join(reportFILES))
        print('RDEPENDS found for module %s:' % pymodule)
        print(''.join(reportRDEPS))
        print('#################################')

print('The following FILES contain wildcards, please check if they are necessary')
print(wildcards)
print('The following FILES contain folders, please check if they are necessary')
print(hasfolders)


# Sort it just so it looks nicer
for pypkg in new_manifest:
    new_manifest[pypkg]['files'].sort()
    new_manifest[pypkg]['cached'].sort()
    new_manifest[pypkg]['rdepends'].sort()

# Create the manifest from the data structure that was built
with open('python3-manifest.json.new','w') as outfile:
    json.dump(new_manifest,outfile, indent=4)
    outfile.write('\n')

prepend_comments(comments,'python3-manifest.json.new')

if (repeated):
    error_msg = '\n\nERROR:\n'
    error_msg += 'The following files were found in more than one package),\n'
    error_msg += 'this is likely to happen when new files are introduced after an upgrade,\n'
    error_msg += 'please check which package should get it,\n modify the manifest accordingly and re-run the create_manifest task:\n'
    error_msg += '\n'.join(repeated)
    error_msg += '\n'
    sys.exit(error_msg)

