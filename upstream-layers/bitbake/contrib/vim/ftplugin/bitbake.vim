" Only do this when not done yet for this buffer
if exists("b:did_ftplugin")
  finish
endif

" Don't load another plugin for this buffer
let b:did_ftplugin = 1

let b:undo_ftplugin = "setl cms< sts< sw< et< sua<"

setlocal commentstring=#\ %s
setlocal softtabstop=4 shiftwidth=4 expandtab
setlocal suffixesadd+=.bb,.bbclass
