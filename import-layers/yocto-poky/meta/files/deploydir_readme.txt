Files in the deploy directory will not be re-created automatically if you
delete them. If you do delete a file, you will need to run:

  bitbake -c clean TARGET
  bitbake TARGET

where TARGET is the name of the appropriate package or target e.g.
"virtual/kernel" for the kernel, an image, etc.
