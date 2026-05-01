.data
message: .asciiz "You liberated a pokemon!"
lose: .asciiz "You failed to liberate a pokemon!"
newline: .asciiz "\n"
.text

noquit:
enc
bat
libsuc win
tra lose
tra newline
jump noquit


win:
lib $t1, 1
tra message


End:
