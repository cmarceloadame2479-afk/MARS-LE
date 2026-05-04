.data
message: .asciiz "You liberated a pokemon!"
lose: .asciiz "You failed to liberate a pokemon!"
newline: .asciiz "\n"
picture:   .asciiz "B2W2_Team_Plasma.bmp"

.text

noquit:
enc
bat
libsuc win
tra lose
tra newline
jump noquit
grunt picture

win:
lib $t1, 1
tra message


End:
