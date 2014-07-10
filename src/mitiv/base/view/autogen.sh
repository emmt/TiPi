#! /bin/sh
for src in FloatView*.java; do
    for Typ in Double Long Int Short Byte; do
        typ=$(echo $Typ | tr [[:upper:]] [[:lower:]])
        dst=$(echo "$src" | sed "s/^Float/${Typ}/")
        sed "s/float/${typ}/g;s/Float/${Typ}/g" <"$src" >"$dst"
    done
done
