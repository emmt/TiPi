# Makefile --
#
# Rules for building array code of TiPi.
#
# *IMPORTANT*   This file has been generated from Makefile.x with
# *IMPORTANT*   tpp, do not edit by hand (modify Makefile.x instead
# *IMPORTANT*   and follow the instructions there to regenerate
# *IMPORTANT*   this Makefile).
#
#CODGER =./tpp --autopkg --docfilter
CODGER =./tpp --warning

# Directories:
ROOT = ../src/mitiv/
ARRAY = $(ROOT)array/
ARRAY_IMPL = $(ROOT)array/impl/
BASE = $(ROOT)base/
MAPPING = $(BASE)mapping/

RANKS = 1 2 3 4 5 6 7 8 9
TYPES = Byte Short Int Long Float Double ComplexFloat ComplexDouble

COMMON_INPUTS = common.javax Makefile

ARRAY_OUTPUTS = $(ARRAY)Scalar.java \
                $(foreach RANK,$(RANKS),$(ARRAY)Array$(RANK)D.java)

TYPED_OUTPUTS = $(ARRAY)@TYPE@Array.java $(ARRAY)@TYPE@Scalar.java \
                $(foreach RANK,$(RANKS),$(ARRAY)@TYPE@$(RANK)D.java) \
                $(foreach RANK,$(RANKS),$(ARRAY_IMPL)Flat@TYPE@$(RANK)D.java) \
                $(foreach RANK,$(RANKS),$(ARRAY_IMPL)Selected@TYPE@$(RANK)D.java) \
                $(foreach RANK,$(RANKS),$(ARRAY_IMPL)Stridden@TYPE@$(RANK)D.java)

MISC_OUTPUTS = $(ARRAY)ArrayFactory.java \
               $(ARRAY)ArrayUtils.java \
               $(BASE)Shape.java \
               $(ROOT)cost/HyperbolicTotalVariation.java \
               $(ROOT)io/ColorModel.java \
               $(ROOT)io/DataFormat.java

BYTE_OUTPUTS = $(subst @TYPE@,Byte,$(TYPED_OUTPUTS))

SHORT_OUTPUTS = $(subst @TYPE@,Short,$(TYPED_OUTPUTS))

INT_OUTPUTS = $(subst @TYPE@,Int,$(TYPED_OUTPUTS))

LONG_OUTPUTS = $(subst @TYPE@,Long,$(TYPED_OUTPUTS))

FLOAT_OUTPUTS = $(subst @TYPE@,Float,$(TYPED_OUTPUTS))

DOUBLE_OUTPUTS = $(subst @TYPE@,Double,$(TYPED_OUTPUTS))

COMPLEXFLOAT_OUTPUTS = $(subst @TYPE@,ComplexFloat,$(TYPED_OUTPUTS))

COMPLEXDOUBLE_OUTPUTS = $(subst @TYPE@,ComplexDouble,$(TYPED_OUTPUTS))

ARRAY_INPUTS = ArrayRank.javax $(COMMON_INPUTS)

TYPED_ARRAY_INPUTS = TypeArray.javax $(COMMON_INPUTS)

TYPE_RANK_INPUTS = TypeRank.javax $(COMMON_INPUTS)

TYPE_SCALAR_INPUTS = TypeScalar.javax $(COMMON_INPUTS)

ARRAY_IMPL_INPUTS = commonImpl.javax commonLoops.javax $(COMMON_INPUTS)

FLAT_ARRAY_INPUTS = FlatArray.javax $(ARRAY_IMPL_INPUTS)

STRIDDEN_ARRAY_INPUTS = StriddenArray.javax $(ARRAY_IMPL_INPUTS)

SELECTED_ARRAY_INPUTS = SelectedArray.javax $(ARRAY_IMPL_INPUTS)

CONVOLUTION_DIR = $(ROOT)deconv/
CONVOLUTION_RANKS = 1 2 3
CONVOLUTION_TYPES = Float Double
CONVOLUTION_OUTPUTS = $(foreach TYPE, $(CONVOLUTION_TYPES), \
                          $(CONVOLUTION_DIR)Convolution$(TYPE).java) \
                      $(foreach TYPE, $(CONVOLUTION_TYPES), \
                          $(foreach RANK, $(CONVOLUTION_RANKS), \
                              $(CONVOLUTION_DIR)Convolution$(TYPE)$(RANK)D.java)) \
                      $(foreach TYPE, $(CONVOLUTION_TYPES), \
                          $(CONVOLUTION_DIR)WeightedConvolution$(TYPE).java) \
                      $(foreach TYPE, $(CONVOLUTION_TYPES), \
                          $(foreach RANK, $(CONVOLUTION_RANKS), \
                              $(CONVOLUTION_DIR)WeightedConvolution$(TYPE)$(RANK)D.java))

MAPPING_OUTPUTS = $(foreach TYPE, $(TYPES), $(MAPPING)$(TYPE)Scanner.java) \
                  $(foreach TYPE, $(TYPES), $(MAPPING)$(TYPE)Function.java)
SCANNER_INPUTS = TypeScanner.javax $(COMMON_INPUTS)
FUNCTION_INPUTS = TypeFunction.javax $(COMMON_INPUTS)


default:
	@echo "No default target, try:"
	@echo "     make all"

all: all-array all-Byte all-Short all-Int all-Long all-Float all-Double all-ComplexFloat all-ComplexDouble \
     all-misc all-convolution all-mapping

clean:
	rm -f *~

.PHONY: default all clean

#-----------------------------------------------------------------------------
# Miscellaneaous

all-misc: $(MISC_OUTPUTS)

Makefile: Makefile.x ./tpp
	$(RM) $@
	./tpp $< $@
	chmod 444 $@

$(ARRAY)ArrayFactory.java: ArrayFactory.javax Makefile
	$(CODGER) -Dpackage=mitiv.array $< $@

$(BASE)Shape.java: Shape.javax
	$(CODGER) --autopkg $< $@

$(ARRAY)ArrayUtils.java: ArrayUtils.javax $(COMMON_INPUTS)
	$(CODGER) -Dpackage=mitiv.array $< $@

$(ROOT)cost/HyperbolicTotalVariation.java: HyperbolicTotalVariation.javax
	$(CODGER) -Dpackage=mitiv.cost $< $@

$(ROOT)io/ColorModel.java: ColorModel.javax $(COMMON_INPUTS)
	$(CODGER) -Dpackage=mitiv.io $< $@

$(ROOT)io/DataFormat.java: DataFormat.javax $(COMMON_INPUTS)
	$(CODGER) -Dpackage=mitiv.io $< $@

#-----------------------------------------------------------------------------
# Convolution operators

all-convolution: $(CONVOLUTION_OUTPUTS)

$(CONVOLUTION_DIR)ConvolutionFloat.java: ConvolutionType.javax $(COMMON_INPUTS)
	$(CODGER) --autopkg -DclassName=ConvolutionFloat -Dtype=float $< $@

$(CONVOLUTION_DIR)ConvolutionFloat1D.java: ConvolutionTypeRank.javax $(COMMON_INPUTS)
	$(CODGER) --autopkg -DclassName=ConvolutionFloat1D -Drank=1 -Dtype=float $< $@
$(CONVOLUTION_DIR)ConvolutionFloat2D.java: ConvolutionTypeRank.javax $(COMMON_INPUTS)
	$(CODGER) --autopkg -DclassName=ConvolutionFloat2D -Drank=2 -Dtype=float $< $@
$(CONVOLUTION_DIR)ConvolutionFloat3D.java: ConvolutionTypeRank.javax $(COMMON_INPUTS)
	$(CODGER) --autopkg -DclassName=ConvolutionFloat3D -Drank=3 -Dtype=float $< $@
$(CONVOLUTION_DIR)ConvolutionDouble.java: ConvolutionType.javax $(COMMON_INPUTS)
	$(CODGER) --autopkg -DclassName=ConvolutionDouble -Dtype=double $< $@

$(CONVOLUTION_DIR)ConvolutionDouble1D.java: ConvolutionTypeRank.javax $(COMMON_INPUTS)
	$(CODGER) --autopkg -DclassName=ConvolutionDouble1D -Drank=1 -Dtype=double $< $@
$(CONVOLUTION_DIR)ConvolutionDouble2D.java: ConvolutionTypeRank.javax $(COMMON_INPUTS)
	$(CODGER) --autopkg -DclassName=ConvolutionDouble2D -Drank=2 -Dtype=double $< $@
$(CONVOLUTION_DIR)ConvolutionDouble3D.java: ConvolutionTypeRank.javax $(COMMON_INPUTS)
	$(CODGER) --autopkg -DclassName=ConvolutionDouble3D -Drank=3 -Dtype=double $< $@

$(CONVOLUTION_DIR)WeightedConvolutionFloat.java: WeightedConvolutionType.javax $(COMMON_INPUTS)
	$(CODGER) --autopkg -DclassName=WeightedConvolutionFloat -Dtype=float $< $@

$(CONVOLUTION_DIR)WeightedConvolutionFloat1D.java: WeightedConvolutionTypeRank.javax $(COMMON_INPUTS)
	$(CODGER) --autopkg -DclassName=WeightedConvolutionFloat1D -Drank=1 -Dtype=float $< $@
$(CONVOLUTION_DIR)WeightedConvolutionFloat2D.java: WeightedConvolutionTypeRank.javax $(COMMON_INPUTS)
	$(CODGER) --autopkg -DclassName=WeightedConvolutionFloat2D -Drank=2 -Dtype=float $< $@
$(CONVOLUTION_DIR)WeightedConvolutionFloat3D.java: WeightedConvolutionTypeRank.javax $(COMMON_INPUTS)
	$(CODGER) --autopkg -DclassName=WeightedConvolutionFloat3D -Drank=3 -Dtype=float $< $@
$(CONVOLUTION_DIR)WeightedConvolutionDouble.java: WeightedConvolutionType.javax $(COMMON_INPUTS)
	$(CODGER) --autopkg -DclassName=WeightedConvolutionDouble -Dtype=double $< $@

$(CONVOLUTION_DIR)WeightedConvolutionDouble1D.java: WeightedConvolutionTypeRank.javax $(COMMON_INPUTS)
	$(CODGER) --autopkg -DclassName=WeightedConvolutionDouble1D -Drank=1 -Dtype=double $< $@
$(CONVOLUTION_DIR)WeightedConvolutionDouble2D.java: WeightedConvolutionTypeRank.javax $(COMMON_INPUTS)
	$(CODGER) --autopkg -DclassName=WeightedConvolutionDouble2D -Drank=2 -Dtype=double $< $@
$(CONVOLUTION_DIR)WeightedConvolutionDouble3D.java: WeightedConvolutionTypeRank.javax $(COMMON_INPUTS)
	$(CODGER) --autopkg -DclassName=WeightedConvolutionDouble3D -Drank=3 -Dtype=double $< $@

#-----------------------------------------------------------------------------
# Array

all-array: $(ARRAY_OUTPUTS)

$(ARRAY)Scalar.java: $(ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Drank=0 $< $@

$(ARRAY)Array1D.java: $(ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Drank=1 $< $@

$(ARRAY)Array2D.java: $(ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Drank=2 $< $@

$(ARRAY)Array3D.java: $(ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Drank=3 $< $@

$(ARRAY)Array4D.java: $(ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Drank=4 $< $@

$(ARRAY)Array5D.java: $(ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Drank=5 $< $@

$(ARRAY)Array6D.java: $(ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Drank=6 $< $@

$(ARRAY)Array7D.java: $(ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Drank=7 $< $@

$(ARRAY)Array8D.java: $(ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Drank=8 $< $@

$(ARRAY)Array9D.java: $(ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Drank=9 $< $@

#-----------------------------------------------------------------------------
# Byte

all-Byte: $(BYTE_OUTPUTS)

$(ARRAY)ByteArray.java: $(TYPED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=byte $< $@

$(ARRAY)ByteScalar.java: $(TYPE_SCALAR_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=byte $< $@

$(ARRAY)Byte1D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=byte -Drank=1 $< $@

$(ARRAY)Byte2D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=byte -Drank=2 $< $@

$(ARRAY)Byte3D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=byte -Drank=3 $< $@

$(ARRAY)Byte4D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=byte -Drank=4 $< $@

$(ARRAY)Byte5D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=byte -Drank=5 $< $@

$(ARRAY)Byte6D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=byte -Drank=6 $< $@

$(ARRAY)Byte7D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=byte -Drank=7 $< $@

$(ARRAY)Byte8D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=byte -Drank=8 $< $@

$(ARRAY)Byte9D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=byte -Drank=9 $< $@

$(ARRAY_IMPL)FlatByte1D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=byte -Drank=1 $< $@

$(ARRAY_IMPL)FlatByte2D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=byte -Drank=2 $< $@

$(ARRAY_IMPL)FlatByte3D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=byte -Drank=3 $< $@

$(ARRAY_IMPL)FlatByte4D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=byte -Drank=4 $< $@

$(ARRAY_IMPL)FlatByte5D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=byte -Drank=5 $< $@

$(ARRAY_IMPL)FlatByte6D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=byte -Drank=6 $< $@

$(ARRAY_IMPL)FlatByte7D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=byte -Drank=7 $< $@

$(ARRAY_IMPL)FlatByte8D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=byte -Drank=8 $< $@

$(ARRAY_IMPL)FlatByte9D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=byte -Drank=9 $< $@

$(ARRAY_IMPL)SelectedByte1D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=byte -Drank=1 $< $@

$(ARRAY_IMPL)SelectedByte2D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=byte -Drank=2 $< $@

$(ARRAY_IMPL)SelectedByte3D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=byte -Drank=3 $< $@

$(ARRAY_IMPL)SelectedByte4D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=byte -Drank=4 $< $@

$(ARRAY_IMPL)SelectedByte5D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=byte -Drank=5 $< $@

$(ARRAY_IMPL)SelectedByte6D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=byte -Drank=6 $< $@

$(ARRAY_IMPL)SelectedByte7D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=byte -Drank=7 $< $@

$(ARRAY_IMPL)SelectedByte8D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=byte -Drank=8 $< $@

$(ARRAY_IMPL)SelectedByte9D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=byte -Drank=9 $< $@

$(ARRAY_IMPL)StriddenByte1D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=byte -Drank=1 $< $@

$(ARRAY_IMPL)StriddenByte2D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=byte -Drank=2 $< $@

$(ARRAY_IMPL)StriddenByte3D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=byte -Drank=3 $< $@

$(ARRAY_IMPL)StriddenByte4D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=byte -Drank=4 $< $@

$(ARRAY_IMPL)StriddenByte5D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=byte -Drank=5 $< $@

$(ARRAY_IMPL)StriddenByte6D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=byte -Drank=6 $< $@

$(ARRAY_IMPL)StriddenByte7D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=byte -Drank=7 $< $@

$(ARRAY_IMPL)StriddenByte8D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=byte -Drank=8 $< $@

$(ARRAY_IMPL)StriddenByte9D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=byte -Drank=9 $< $@

#-----------------------------------------------------------------------------
# Short

all-Short: $(SHORT_OUTPUTS)

$(ARRAY)ShortArray.java: $(TYPED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=short $< $@

$(ARRAY)ShortScalar.java: $(TYPE_SCALAR_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=short $< $@

$(ARRAY)Short1D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=short -Drank=1 $< $@

$(ARRAY)Short2D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=short -Drank=2 $< $@

$(ARRAY)Short3D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=short -Drank=3 $< $@

$(ARRAY)Short4D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=short -Drank=4 $< $@

$(ARRAY)Short5D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=short -Drank=5 $< $@

$(ARRAY)Short6D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=short -Drank=6 $< $@

$(ARRAY)Short7D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=short -Drank=7 $< $@

$(ARRAY)Short8D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=short -Drank=8 $< $@

$(ARRAY)Short9D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=short -Drank=9 $< $@

$(ARRAY_IMPL)FlatShort1D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=short -Drank=1 $< $@

$(ARRAY_IMPL)FlatShort2D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=short -Drank=2 $< $@

$(ARRAY_IMPL)FlatShort3D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=short -Drank=3 $< $@

$(ARRAY_IMPL)FlatShort4D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=short -Drank=4 $< $@

$(ARRAY_IMPL)FlatShort5D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=short -Drank=5 $< $@

$(ARRAY_IMPL)FlatShort6D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=short -Drank=6 $< $@

$(ARRAY_IMPL)FlatShort7D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=short -Drank=7 $< $@

$(ARRAY_IMPL)FlatShort8D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=short -Drank=8 $< $@

$(ARRAY_IMPL)FlatShort9D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=short -Drank=9 $< $@

$(ARRAY_IMPL)SelectedShort1D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=short -Drank=1 $< $@

$(ARRAY_IMPL)SelectedShort2D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=short -Drank=2 $< $@

$(ARRAY_IMPL)SelectedShort3D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=short -Drank=3 $< $@

$(ARRAY_IMPL)SelectedShort4D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=short -Drank=4 $< $@

$(ARRAY_IMPL)SelectedShort5D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=short -Drank=5 $< $@

$(ARRAY_IMPL)SelectedShort6D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=short -Drank=6 $< $@

$(ARRAY_IMPL)SelectedShort7D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=short -Drank=7 $< $@

$(ARRAY_IMPL)SelectedShort8D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=short -Drank=8 $< $@

$(ARRAY_IMPL)SelectedShort9D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=short -Drank=9 $< $@

$(ARRAY_IMPL)StriddenShort1D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=short -Drank=1 $< $@

$(ARRAY_IMPL)StriddenShort2D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=short -Drank=2 $< $@

$(ARRAY_IMPL)StriddenShort3D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=short -Drank=3 $< $@

$(ARRAY_IMPL)StriddenShort4D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=short -Drank=4 $< $@

$(ARRAY_IMPL)StriddenShort5D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=short -Drank=5 $< $@

$(ARRAY_IMPL)StriddenShort6D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=short -Drank=6 $< $@

$(ARRAY_IMPL)StriddenShort7D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=short -Drank=7 $< $@

$(ARRAY_IMPL)StriddenShort8D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=short -Drank=8 $< $@

$(ARRAY_IMPL)StriddenShort9D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=short -Drank=9 $< $@

#-----------------------------------------------------------------------------
# Int

all-Int: $(INT_OUTPUTS)

$(ARRAY)IntArray.java: $(TYPED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=int $< $@

$(ARRAY)IntScalar.java: $(TYPE_SCALAR_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=int $< $@

$(ARRAY)Int1D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=int -Drank=1 $< $@

$(ARRAY)Int2D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=int -Drank=2 $< $@

$(ARRAY)Int3D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=int -Drank=3 $< $@

$(ARRAY)Int4D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=int -Drank=4 $< $@

$(ARRAY)Int5D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=int -Drank=5 $< $@

$(ARRAY)Int6D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=int -Drank=6 $< $@

$(ARRAY)Int7D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=int -Drank=7 $< $@

$(ARRAY)Int8D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=int -Drank=8 $< $@

$(ARRAY)Int9D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=int -Drank=9 $< $@

$(ARRAY_IMPL)FlatInt1D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=int -Drank=1 $< $@

$(ARRAY_IMPL)FlatInt2D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=int -Drank=2 $< $@

$(ARRAY_IMPL)FlatInt3D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=int -Drank=3 $< $@

$(ARRAY_IMPL)FlatInt4D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=int -Drank=4 $< $@

$(ARRAY_IMPL)FlatInt5D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=int -Drank=5 $< $@

$(ARRAY_IMPL)FlatInt6D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=int -Drank=6 $< $@

$(ARRAY_IMPL)FlatInt7D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=int -Drank=7 $< $@

$(ARRAY_IMPL)FlatInt8D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=int -Drank=8 $< $@

$(ARRAY_IMPL)FlatInt9D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=int -Drank=9 $< $@

$(ARRAY_IMPL)SelectedInt1D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=int -Drank=1 $< $@

$(ARRAY_IMPL)SelectedInt2D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=int -Drank=2 $< $@

$(ARRAY_IMPL)SelectedInt3D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=int -Drank=3 $< $@

$(ARRAY_IMPL)SelectedInt4D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=int -Drank=4 $< $@

$(ARRAY_IMPL)SelectedInt5D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=int -Drank=5 $< $@

$(ARRAY_IMPL)SelectedInt6D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=int -Drank=6 $< $@

$(ARRAY_IMPL)SelectedInt7D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=int -Drank=7 $< $@

$(ARRAY_IMPL)SelectedInt8D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=int -Drank=8 $< $@

$(ARRAY_IMPL)SelectedInt9D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=int -Drank=9 $< $@

$(ARRAY_IMPL)StriddenInt1D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=int -Drank=1 $< $@

$(ARRAY_IMPL)StriddenInt2D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=int -Drank=2 $< $@

$(ARRAY_IMPL)StriddenInt3D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=int -Drank=3 $< $@

$(ARRAY_IMPL)StriddenInt4D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=int -Drank=4 $< $@

$(ARRAY_IMPL)StriddenInt5D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=int -Drank=5 $< $@

$(ARRAY_IMPL)StriddenInt6D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=int -Drank=6 $< $@

$(ARRAY_IMPL)StriddenInt7D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=int -Drank=7 $< $@

$(ARRAY_IMPL)StriddenInt8D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=int -Drank=8 $< $@

$(ARRAY_IMPL)StriddenInt9D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=int -Drank=9 $< $@

#-----------------------------------------------------------------------------
# Long

all-Long: $(LONG_OUTPUTS)

$(ARRAY)LongArray.java: $(TYPED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=long $< $@

$(ARRAY)LongScalar.java: $(TYPE_SCALAR_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=long $< $@

$(ARRAY)Long1D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=long -Drank=1 $< $@

$(ARRAY)Long2D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=long -Drank=2 $< $@

$(ARRAY)Long3D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=long -Drank=3 $< $@

$(ARRAY)Long4D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=long -Drank=4 $< $@

$(ARRAY)Long5D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=long -Drank=5 $< $@

$(ARRAY)Long6D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=long -Drank=6 $< $@

$(ARRAY)Long7D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=long -Drank=7 $< $@

$(ARRAY)Long8D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=long -Drank=8 $< $@

$(ARRAY)Long9D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=long -Drank=9 $< $@

$(ARRAY_IMPL)FlatLong1D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=long -Drank=1 $< $@

$(ARRAY_IMPL)FlatLong2D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=long -Drank=2 $< $@

$(ARRAY_IMPL)FlatLong3D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=long -Drank=3 $< $@

$(ARRAY_IMPL)FlatLong4D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=long -Drank=4 $< $@

$(ARRAY_IMPL)FlatLong5D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=long -Drank=5 $< $@

$(ARRAY_IMPL)FlatLong6D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=long -Drank=6 $< $@

$(ARRAY_IMPL)FlatLong7D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=long -Drank=7 $< $@

$(ARRAY_IMPL)FlatLong8D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=long -Drank=8 $< $@

$(ARRAY_IMPL)FlatLong9D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=long -Drank=9 $< $@

$(ARRAY_IMPL)SelectedLong1D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=long -Drank=1 $< $@

$(ARRAY_IMPL)SelectedLong2D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=long -Drank=2 $< $@

$(ARRAY_IMPL)SelectedLong3D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=long -Drank=3 $< $@

$(ARRAY_IMPL)SelectedLong4D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=long -Drank=4 $< $@

$(ARRAY_IMPL)SelectedLong5D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=long -Drank=5 $< $@

$(ARRAY_IMPL)SelectedLong6D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=long -Drank=6 $< $@

$(ARRAY_IMPL)SelectedLong7D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=long -Drank=7 $< $@

$(ARRAY_IMPL)SelectedLong8D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=long -Drank=8 $< $@

$(ARRAY_IMPL)SelectedLong9D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=long -Drank=9 $< $@

$(ARRAY_IMPL)StriddenLong1D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=long -Drank=1 $< $@

$(ARRAY_IMPL)StriddenLong2D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=long -Drank=2 $< $@

$(ARRAY_IMPL)StriddenLong3D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=long -Drank=3 $< $@

$(ARRAY_IMPL)StriddenLong4D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=long -Drank=4 $< $@

$(ARRAY_IMPL)StriddenLong5D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=long -Drank=5 $< $@

$(ARRAY_IMPL)StriddenLong6D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=long -Drank=6 $< $@

$(ARRAY_IMPL)StriddenLong7D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=long -Drank=7 $< $@

$(ARRAY_IMPL)StriddenLong8D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=long -Drank=8 $< $@

$(ARRAY_IMPL)StriddenLong9D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=long -Drank=9 $< $@

#-----------------------------------------------------------------------------
# Float

all-Float: $(FLOAT_OUTPUTS)

$(ARRAY)FloatArray.java: $(TYPED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float $< $@

$(ARRAY)FloatScalar.java: $(TYPE_SCALAR_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float $< $@

$(ARRAY)Float1D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=1 $< $@

$(ARRAY)Float2D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=2 $< $@

$(ARRAY)Float3D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=3 $< $@

$(ARRAY)Float4D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=4 $< $@

$(ARRAY)Float5D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=5 $< $@

$(ARRAY)Float6D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=6 $< $@

$(ARRAY)Float7D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=7 $< $@

$(ARRAY)Float8D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=8 $< $@

$(ARRAY)Float9D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=9 $< $@

$(ARRAY_IMPL)FlatFloat1D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=1 $< $@

$(ARRAY_IMPL)FlatFloat2D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=2 $< $@

$(ARRAY_IMPL)FlatFloat3D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=3 $< $@

$(ARRAY_IMPL)FlatFloat4D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=4 $< $@

$(ARRAY_IMPL)FlatFloat5D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=5 $< $@

$(ARRAY_IMPL)FlatFloat6D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=6 $< $@

$(ARRAY_IMPL)FlatFloat7D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=7 $< $@

$(ARRAY_IMPL)FlatFloat8D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=8 $< $@

$(ARRAY_IMPL)FlatFloat9D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=9 $< $@

$(ARRAY_IMPL)SelectedFloat1D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=1 $< $@

$(ARRAY_IMPL)SelectedFloat2D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=2 $< $@

$(ARRAY_IMPL)SelectedFloat3D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=3 $< $@

$(ARRAY_IMPL)SelectedFloat4D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=4 $< $@

$(ARRAY_IMPL)SelectedFloat5D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=5 $< $@

$(ARRAY_IMPL)SelectedFloat6D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=6 $< $@

$(ARRAY_IMPL)SelectedFloat7D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=7 $< $@

$(ARRAY_IMPL)SelectedFloat8D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=8 $< $@

$(ARRAY_IMPL)SelectedFloat9D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=9 $< $@

$(ARRAY_IMPL)StriddenFloat1D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=1 $< $@

$(ARRAY_IMPL)StriddenFloat2D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=2 $< $@

$(ARRAY_IMPL)StriddenFloat3D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=3 $< $@

$(ARRAY_IMPL)StriddenFloat4D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=4 $< $@

$(ARRAY_IMPL)StriddenFloat5D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=5 $< $@

$(ARRAY_IMPL)StriddenFloat6D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=6 $< $@

$(ARRAY_IMPL)StriddenFloat7D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=7 $< $@

$(ARRAY_IMPL)StriddenFloat8D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=8 $< $@

$(ARRAY_IMPL)StriddenFloat9D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=9 $< $@

#-----------------------------------------------------------------------------
# Double

all-Double: $(DOUBLE_OUTPUTS)

$(ARRAY)DoubleArray.java: $(TYPED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double $< $@

$(ARRAY)DoubleScalar.java: $(TYPE_SCALAR_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double $< $@

$(ARRAY)Double1D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=1 $< $@

$(ARRAY)Double2D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=2 $< $@

$(ARRAY)Double3D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=3 $< $@

$(ARRAY)Double4D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=4 $< $@

$(ARRAY)Double5D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=5 $< $@

$(ARRAY)Double6D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=6 $< $@

$(ARRAY)Double7D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=7 $< $@

$(ARRAY)Double8D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=8 $< $@

$(ARRAY)Double9D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=9 $< $@

$(ARRAY_IMPL)FlatDouble1D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=1 $< $@

$(ARRAY_IMPL)FlatDouble2D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=2 $< $@

$(ARRAY_IMPL)FlatDouble3D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=3 $< $@

$(ARRAY_IMPL)FlatDouble4D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=4 $< $@

$(ARRAY_IMPL)FlatDouble5D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=5 $< $@

$(ARRAY_IMPL)FlatDouble6D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=6 $< $@

$(ARRAY_IMPL)FlatDouble7D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=7 $< $@

$(ARRAY_IMPL)FlatDouble8D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=8 $< $@

$(ARRAY_IMPL)FlatDouble9D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=9 $< $@

$(ARRAY_IMPL)SelectedDouble1D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=1 $< $@

$(ARRAY_IMPL)SelectedDouble2D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=2 $< $@

$(ARRAY_IMPL)SelectedDouble3D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=3 $< $@

$(ARRAY_IMPL)SelectedDouble4D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=4 $< $@

$(ARRAY_IMPL)SelectedDouble5D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=5 $< $@

$(ARRAY_IMPL)SelectedDouble6D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=6 $< $@

$(ARRAY_IMPL)SelectedDouble7D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=7 $< $@

$(ARRAY_IMPL)SelectedDouble8D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=8 $< $@

$(ARRAY_IMPL)SelectedDouble9D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=9 $< $@

$(ARRAY_IMPL)StriddenDouble1D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=1 $< $@

$(ARRAY_IMPL)StriddenDouble2D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=2 $< $@

$(ARRAY_IMPL)StriddenDouble3D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=3 $< $@

$(ARRAY_IMPL)StriddenDouble4D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=4 $< $@

$(ARRAY_IMPL)StriddenDouble5D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=5 $< $@

$(ARRAY_IMPL)StriddenDouble6D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=6 $< $@

$(ARRAY_IMPL)StriddenDouble7D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=7 $< $@

$(ARRAY_IMPL)StriddenDouble8D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=8 $< $@

$(ARRAY_IMPL)StriddenDouble9D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=9 $< $@

#-----------------------------------------------------------------------------
# ComplexFloat

all-ComplexFloat: $(COMPLEXFLOAT_OUTPUTS)

$(ARRAY)ComplexFloatArray.java: $(TYPED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float $< $@

$(ARRAY)ComplexFloatScalar.java: $(TYPE_SCALAR_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float $< $@

$(ARRAY)ComplexFloat1D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=1 $< $@

$(ARRAY)ComplexFloat2D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=2 $< $@

$(ARRAY)ComplexFloat3D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=3 $< $@

$(ARRAY)ComplexFloat4D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=4 $< $@

$(ARRAY)ComplexFloat5D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=5 $< $@

$(ARRAY)ComplexFloat6D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=6 $< $@

$(ARRAY)ComplexFloat7D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=7 $< $@

$(ARRAY)ComplexFloat8D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=8 $< $@

$(ARRAY)ComplexFloat9D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=9 $< $@

$(ARRAY_IMPL)FlatComplexFloat1D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=1 $< $@

$(ARRAY_IMPL)FlatComplexFloat2D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=2 $< $@

$(ARRAY_IMPL)FlatComplexFloat3D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=3 $< $@

$(ARRAY_IMPL)FlatComplexFloat4D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=4 $< $@

$(ARRAY_IMPL)FlatComplexFloat5D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=5 $< $@

$(ARRAY_IMPL)FlatComplexFloat6D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=6 $< $@

$(ARRAY_IMPL)FlatComplexFloat7D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=7 $< $@

$(ARRAY_IMPL)FlatComplexFloat8D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=8 $< $@

$(ARRAY_IMPL)FlatComplexFloat9D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=9 $< $@

$(ARRAY_IMPL)SelectedComplexFloat1D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=1 $< $@

$(ARRAY_IMPL)SelectedComplexFloat2D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=2 $< $@

$(ARRAY_IMPL)SelectedComplexFloat3D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=3 $< $@

$(ARRAY_IMPL)SelectedComplexFloat4D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=4 $< $@

$(ARRAY_IMPL)SelectedComplexFloat5D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=5 $< $@

$(ARRAY_IMPL)SelectedComplexFloat6D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=6 $< $@

$(ARRAY_IMPL)SelectedComplexFloat7D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=7 $< $@

$(ARRAY_IMPL)SelectedComplexFloat8D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=8 $< $@

$(ARRAY_IMPL)SelectedComplexFloat9D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=9 $< $@

$(ARRAY_IMPL)StriddenComplexFloat1D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=1 $< $@

$(ARRAY_IMPL)StriddenComplexFloat2D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=2 $< $@

$(ARRAY_IMPL)StriddenComplexFloat3D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=3 $< $@

$(ARRAY_IMPL)StriddenComplexFloat4D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=4 $< $@

$(ARRAY_IMPL)StriddenComplexFloat5D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=5 $< $@

$(ARRAY_IMPL)StriddenComplexFloat6D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=6 $< $@

$(ARRAY_IMPL)StriddenComplexFloat7D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=7 $< $@

$(ARRAY_IMPL)StriddenComplexFloat8D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=8 $< $@

$(ARRAY_IMPL)StriddenComplexFloat9D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=float -Drank=9 $< $@

#-----------------------------------------------------------------------------
# ComplexDouble

all-ComplexDouble: $(COMPLEXDOUBLE_OUTPUTS)

$(ARRAY)ComplexDoubleArray.java: $(TYPED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double $< $@

$(ARRAY)ComplexDoubleScalar.java: $(TYPE_SCALAR_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double $< $@

$(ARRAY)ComplexDouble1D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=1 $< $@

$(ARRAY)ComplexDouble2D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=2 $< $@

$(ARRAY)ComplexDouble3D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=3 $< $@

$(ARRAY)ComplexDouble4D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=4 $< $@

$(ARRAY)ComplexDouble5D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=5 $< $@

$(ARRAY)ComplexDouble6D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=6 $< $@

$(ARRAY)ComplexDouble7D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=7 $< $@

$(ARRAY)ComplexDouble8D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=8 $< $@

$(ARRAY)ComplexDouble9D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=9 $< $@

$(ARRAY_IMPL)FlatComplexDouble1D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=1 $< $@

$(ARRAY_IMPL)FlatComplexDouble2D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=2 $< $@

$(ARRAY_IMPL)FlatComplexDouble3D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=3 $< $@

$(ARRAY_IMPL)FlatComplexDouble4D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=4 $< $@

$(ARRAY_IMPL)FlatComplexDouble5D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=5 $< $@

$(ARRAY_IMPL)FlatComplexDouble6D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=6 $< $@

$(ARRAY_IMPL)FlatComplexDouble7D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=7 $< $@

$(ARRAY_IMPL)FlatComplexDouble8D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=8 $< $@

$(ARRAY_IMPL)FlatComplexDouble9D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=9 $< $@

$(ARRAY_IMPL)SelectedComplexDouble1D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=1 $< $@

$(ARRAY_IMPL)SelectedComplexDouble2D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=2 $< $@

$(ARRAY_IMPL)SelectedComplexDouble3D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=3 $< $@

$(ARRAY_IMPL)SelectedComplexDouble4D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=4 $< $@

$(ARRAY_IMPL)SelectedComplexDouble5D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=5 $< $@

$(ARRAY_IMPL)SelectedComplexDouble6D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=6 $< $@

$(ARRAY_IMPL)SelectedComplexDouble7D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=7 $< $@

$(ARRAY_IMPL)SelectedComplexDouble8D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=8 $< $@

$(ARRAY_IMPL)SelectedComplexDouble9D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=9 $< $@

$(ARRAY_IMPL)StriddenComplexDouble1D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=1 $< $@

$(ARRAY_IMPL)StriddenComplexDouble2D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=2 $< $@

$(ARRAY_IMPL)StriddenComplexDouble3D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=3 $< $@

$(ARRAY_IMPL)StriddenComplexDouble4D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=4 $< $@

$(ARRAY_IMPL)StriddenComplexDouble5D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=5 $< $@

$(ARRAY_IMPL)StriddenComplexDouble6D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=6 $< $@

$(ARRAY_IMPL)StriddenComplexDouble7D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=7 $< $@

$(ARRAY_IMPL)StriddenComplexDouble8D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=8 $< $@

$(ARRAY_IMPL)StriddenComplexDouble9D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=double -Drank=9 $< $@


#-----------------------------------------------------------------------------
# Scanners, functions, etc.

all-mapping: $(MAPPING_OUTPUTS)

$(MAPPING)ByteScanner.java: $(SCANNER_INPUTS)
	$(CODGER) --autopkg -Dtype=byte $< $@

$(MAPPING)ShortScanner.java: $(SCANNER_INPUTS)
	$(CODGER) --autopkg -Dtype=short $< $@

$(MAPPING)IntScanner.java: $(SCANNER_INPUTS)
	$(CODGER) --autopkg -Dtype=int $< $@

$(MAPPING)LongScanner.java: $(SCANNER_INPUTS)
	$(CODGER) --autopkg -Dtype=long $< $@

$(MAPPING)FloatScanner.java: $(SCANNER_INPUTS)
	$(CODGER) --autopkg -Dtype=float $< $@

$(MAPPING)DoubleScanner.java: $(SCANNER_INPUTS)
	$(CODGER) --autopkg -Dtype=double $< $@

$(MAPPING)ComplexFloatScanner.java: $(SCANNER_INPUTS)
	$(CODGER) --autopkg -Dtype=float $< $@

$(MAPPING)ComplexDoubleScanner.java: $(SCANNER_INPUTS)
	$(CODGER) --autopkg -Dtype=double $< $@

$(MAPPING)ByteFunction.java: $(FUNCTION_INPUTS)
	$(CODGER) --autopkg -Dtype=byte $< $@

$(MAPPING)ShortFunction.java: $(FUNCTION_INPUTS)
	$(CODGER) --autopkg -Dtype=short $< $@

$(MAPPING)IntFunction.java: $(FUNCTION_INPUTS)
	$(CODGER) --autopkg -Dtype=int $< $@

$(MAPPING)LongFunction.java: $(FUNCTION_INPUTS)
	$(CODGER) --autopkg -Dtype=long $< $@

$(MAPPING)FloatFunction.java: $(FUNCTION_INPUTS)
	$(CODGER) --autopkg -Dtype=float $< $@

$(MAPPING)DoubleFunction.java: $(FUNCTION_INPUTS)
	$(CODGER) --autopkg -Dtype=double $< $@

$(MAPPING)ComplexFloatFunction.java: $(FUNCTION_INPUTS)
	$(CODGER) --autopkg -Dtype=float $< $@

$(MAPPING)ComplexDoubleFunction.java: $(FUNCTION_INPUTS)
	$(CODGER) --autopkg -Dtype=double $< $@

#-----------------------------------------------------------------------------
