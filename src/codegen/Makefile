
#CODGER=./tpp --autopkg --docfilter
CODGER=./tpp -Dpackage=mitiv.array
PFX= ../mitiv/array/

MISC_OUTPUTS = $(PFX)ArrayFactory.java;

ARRAY_OUTPUTS = $(PFX)Array1D.java $(PFX)Array2D.java $(PFX)Array3D.java \
                $(PFX)Array4D.java $(PFX)Array5D.java $(PFX)Array6D.java \
                $(PFX)Array7D.java $(PFX)Array8D.java $(PFX)Array9D.java

BYTE_OUTPUTS = $(PFX)Byte1D.java $(PFX)Byte2D.java $(PFX)Byte3D.java \
               $(PFX)Byte4D.java $(PFX)Byte5D.java $(PFX)Byte6D.java \
               $(PFX)Byte7D.java $(PFX)Byte8D.java $(PFX)Byte9D.java \
               $(PFX)ByteArray.java

SHORT_OUTPUTS = $(PFX)Short1D.java $(PFX)Short2D.java $(PFX)Short3D.java \
                $(PFX)Short4D.java $(PFX)Short5D.java $(PFX)Short6D.java \
                $(PFX)Short7D.java $(PFX)Short8D.java $(PFX)Short9D.java \
                $(PFX)ShortArray.java

INTEGER_OUTPUTS = $(PFX)Int1D.java $(PFX)Int2D.java $(PFX)Int3D.java \
                  $(PFX)Int4D.java $(PFX)Int5D.java $(PFX)Int6D.java \
                  $(PFX)Int7D.java $(PFX)Int8D.java $(PFX)Int9D.java \
                  $(PFX)IntArray.java

LONG_OUTPUTS = $(PFX)Long1D.java $(PFX)Long2D.java $(PFX)Long3D.java \
               $(PFX)Long4D.java $(PFX)Long5D.java $(PFX)Long6D.java \
               $(PFX)Long7D.java $(PFX)Long8D.java $(PFX)Long9D.java \
               $(PFX)LongArray.java

FLOAT_OUTPUTS = $(PFX)Float1D.java $(PFX)Float2D.java $(PFX)Float3D.java \
                $(PFX)Float4D.java $(PFX)Float5D.java $(PFX)Float6D.java \
                $(PFX)Float7D.java $(PFX)Float8D.java $(PFX)Float9D.java \
                $(PFX)FloatArray.java

DOUBLE_OUTPUTS = $(PFX)Double1D.java $(PFX)Double2D.java $(PFX)Double3D.java \
                 $(PFX)Double4D.java $(PFX)Double5D.java $(PFX)Double6D.java \
                 $(PFX)Double7D.java $(PFX)Double8D.java $(PFX)Double9D.java \
                 $(PFX)DoubleArray.java

ARRAY_INPUTS = ArrayND.javax common.javax
TYPED_ARRAY_INPUTS = TypeArray.javax common.javax
TYPE_RANK_INPUTS = TypeND.javax common.javax

default:

all: all-array all-byte all-short all-integer all-long all-float all-double \
     all-misc

clean:
	rm -f *~

#-----------------------------------------------------------------------------
# Meicellaneaous

all-misc: $(MISC_OUTPUTS)

$(PFX)ArrayFactory.java: ArrayFactory.javax
	$(CODGER) $< $@

#-----------------------------------------------------------------------------
# Array

all-array: $(ARRAY_OUTPUTS)

$(PFX)Array1D.java: $(ARRAY_INPUTS)
	$(CODGER) -Drank=1 $< $@

$(PFX)Array2D.java: $(ARRAY_INPUTS)
	$(CODGER) -Drank=2 $< $@

$(PFX)Array3D.java: $(ARRAY_INPUTS)
	$(CODGER) -Drank=3 $< $@

$(PFX)Array4D.java: $(ARRAY_INPUTS)
	$(CODGER) -Drank=4 $< $@

$(PFX)Array5D.java: $(ARRAY_INPUTS)
	$(CODGER) -Drank=5 $< $@

$(PFX)Array6D.java: $(ARRAY_INPUTS)
	$(CODGER) -Drank=6 $< $@

$(PFX)Array7D.java: $(ARRAY_INPUTS)
	$(CODGER) -Drank=7 $< $@

$(PFX)Array8D.java: $(ARRAY_INPUTS)
	$(CODGER) -Drank=8 $< $@

$(PFX)Array9D.java: $(ARRAY_INPUTS)
	$(CODGER) -Drank=9 $< $@

#-----------------------------------------------------------------------------
# Byte

all-byte: $(BYTE_OUTPUTS)

$(PFX)ByteArray.java: $(TYPED_ARRAY_INPUTS)
	$(CODGER) -Dtype=byte $< $@

$(PFX)Byte1D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=byte -Drank=1 $< $@

$(PFX)Byte2D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=byte -Drank=2 $< $@

$(PFX)Byte3D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=byte -Drank=3 $< $@

$(PFX)Byte4D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=byte -Drank=4 $< $@

$(PFX)Byte5D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=byte -Drank=5 $< $@

$(PFX)Byte6D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=byte -Drank=6 $< $@

$(PFX)Byte7D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=byte -Drank=7 $< $@

$(PFX)Byte8D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=byte -Drank=8 $< $@

$(PFX)Byte9D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=byte -Drank=9 $< $@

#-----------------------------------------------------------------------------
# Short

all-short: $(SHORT_OUTPUTS)

$(PFX)ShortArray.java: $(TYPED_ARRAY_INPUTS)
	$(CODGER) -Dtype=short $< $@

$(PFX)Short1D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=short -Drank=1 $< $@

$(PFX)Short2D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=short -Drank=2 $< $@

$(PFX)Short3D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=short -Drank=3 $< $@

$(PFX)Short4D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=short -Drank=4 $< $@

$(PFX)Short5D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=short -Drank=5 $< $@

$(PFX)Short6D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=short -Drank=6 $< $@

$(PFX)Short7D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=short -Drank=7 $< $@

$(PFX)Short8D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=short -Drank=8 $< $@

$(PFX)Short9D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=short -Drank=9 $< $@

#-----------------------------------------------------------------------------
# Int

all-integer: $(INTEGER_OUTPUTS)

$(PFX)IntArray.java: $(TYPED_ARRAY_INPUTS)
	$(CODGER) -Dtype=int $< $@

$(PFX)Int1D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=int -Drank=1 $< $@

$(PFX)Int2D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=int -Drank=2 $< $@

$(PFX)Int3D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=int -Drank=3 $< $@

$(PFX)Int4D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=int -Drank=4 $< $@

$(PFX)Int5D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=int -Drank=5 $< $@

$(PFX)Int6D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=int -Drank=6 $< $@

$(PFX)Int7D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=int -Drank=7 $< $@

$(PFX)Int8D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=int -Drank=8 $< $@

$(PFX)Int9D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=int -Drank=9 $< $@

#-----------------------------------------------------------------------------
# Long

all-long: $(LONG_OUTPUTS)

$(PFX)LongArray.java: $(TYPED_ARRAY_INPUTS)
	$(CODGER) -Dtype=long $< $@

$(PFX)Long1D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=long -Drank=1 $< $@

$(PFX)Long2D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=long -Drank=2 $< $@

$(PFX)Long3D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=long -Drank=3 $< $@

$(PFX)Long4D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=long -Drank=4 $< $@

$(PFX)Long5D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=long -Drank=5 $< $@

$(PFX)Long6D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=long -Drank=6 $< $@

$(PFX)Long7D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=long -Drank=7 $< $@

$(PFX)Long8D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=long -Drank=8 $< $@

$(PFX)Long9D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=long -Drank=9 $< $@

#-----------------------------------------------------------------------------
# Float

all-float: $(FLOAT_OUTPUTS)

$(PFX)FloatArray.java: $(TYPED_ARRAY_INPUTS)
	$(CODGER) -Dtype=float $< $@

$(PFX)Float1D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=float -Drank=1 $< $@

$(PFX)Float2D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=float -Drank=2 $< $@

$(PFX)Float3D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=float -Drank=3 $< $@

$(PFX)Float4D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=float -Drank=4 $< $@

$(PFX)Float5D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=float -Drank=5 $< $@

$(PFX)Float6D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=float -Drank=6 $< $@

$(PFX)Float7D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=float -Drank=7 $< $@

$(PFX)Float8D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=float -Drank=8 $< $@

$(PFX)Float9D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=float -Drank=9 $< $@

#-----------------------------------------------------------------------------
# Double

all-double: $(DOUBLE_OUTPUTS)

$(PFX)DoubleArray.java: $(TYPED_ARRAY_INPUTS)
	$(CODGER) -Dtype=double $< $@

$(PFX)Double1D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=double -Drank=1 $< $@

$(PFX)Double2D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=double -Drank=2 $< $@

$(PFX)Double3D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=double -Drank=3 $< $@

$(PFX)Double4D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=double -Drank=4 $< $@

$(PFX)Double5D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=double -Drank=5 $< $@

$(PFX)Double6D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=double -Drank=6 $< $@

$(PFX)Double7D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=double -Drank=7 $< $@

$(PFX)Double8D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=double -Drank=8 $< $@

$(PFX)Double9D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dtype=double -Drank=9 $< $@

#-----------------------------------------------------------------------------
