//# // This is the source for the Makefile.  To generate Makefile,
//# // just do:
//# //      ./tpp <Makefile.x >Makefile
//#
//# // Definitions for all available types.
//#
//# def BYTE   = 0
//# def type_0 = byte
//# def Type_0 = Byte
//# def TYPE_0 = BYTE
//#
//# def SHORT  = 1
//# def type_1 = short
//# def Type_1 = Short
//# def TYPE_1 = SHORT
//#
//# def INT    = 2
//# def type_2 = int
//# def Type_2 = Int
//# def TYPE_2 = INT
//#
//# def LONG   = 3
//# def type_3 = long
//# def Type_3 = Long
//# def TYPE_3 = LONG
//#
//# def FLOAT  = 4
//# def type_4 = float
//# def Type_4 = Float
//# def TYPE_4 = FLOAT
//#
//# def DOUBLE = 5
//# def type_5 = double
//# def Type_5 = Double
//# def TYPE_5 = DOUBLE
//#
//# // Convert type name into a numerical code.
//# def identof_byte   = ${BYTE}
//# def identof_short  = ${SHORT}
//# def identof_int    = ${INT}
//# def identof_long   = ${LONG}
//# def identof_float  = ${FLOAT}
//# def identof_double = ${DOUBLE}
//#
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
TYPES = Byte Short Int Long Float Double

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

ARRAY_INPUTS = ArrayRank.javax common.javax

TYPED_ARRAY_INPUTS = TypeArray.javax common.javax

TYPE_RANK_INPUTS = TypeRank.javax common.javax

TYPE_SCALAR_INPUTS = TypeScalar.javax common.javax

ARRAY_IMPL_INPUTS = commonImpl.javax commonLoops.javax common.javax

FLAT_ARRAY_INPUTS = FlatArray.javax $(ARRAY_IMPL_INPUTS)

STRIDDEN_ARRAY_INPUTS = StriddenArray.javax $(ARRAY_IMPL_INPUTS)

SELECTED_ARRAY_INPUTS = SelectedArray.javax $(ARRAY_IMPL_INPUTS)

CONVOLUTION_DIR = $(ROOT)conv/
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
SCANNER_INPUTS = TypeScanner.javax common.javax
FUNCTION_INPUTS = TypeFunction.javax common.javax


default:
	@echo "No default target, try:"
	@echo "     make all"

all: all-array all-byte all-short all-int all-long all-float all-double \
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

$(ARRAY)ArrayFactory.java: ArrayFactory.javax
	$(CODGER) -Dpackage=mitiv.array $< $@

$(BASE)Shape.java: Shape.javax
	$(CODGER) --autopkg $< $@

$(ARRAY)ArrayUtils.java: ArrayUtils.javax common.javax
	$(CODGER) -Dpackage=mitiv.array $< $@

$(ROOT)cost/HyperbolicTotalVariation.java: HyperbolicTotalVariation.javax
	$(CODGER) -Dpackage=mitiv.cost $< $@

$(ROOT)io/ColorModel.java: ColorModel.javax common.javax
	$(CODGER) -Dpackage=mitiv.io $< $@

$(ROOT)io/DataFormat.java: DataFormat.javax common.javax
	$(CODGER) -Dpackage=mitiv.io $< $@

#-----------------------------------------------------------------------------
# Convolution operators

all-convolution: $(CONVOLUTION_OUTPUTS)

//# for typeId in ${FLOAT} ${DOUBLE}
//#     def type = ${}{type_${typeId}}
//#     def type = ${type}
//#     def Type = ${}{Type_${typeId}}
//#     def Type = ${Type}
//#     def TYPE = ${}{TYPE_${typeId}}
//#     def TYPE = ${TYPE}
$(CONVOLUTION_DIR)Convolution${Type}.java: ConvolutionType.javax common.javax
	$(CODGER) --autopkg -DclassName=Convolution${Type} -Dtype=${type} $< $@

//#     for rank in 1:3
$(CONVOLUTION_DIR)Convolution${Type}${rank}D.java: ConvolutionTypeRank.javax common.javax
	$(CODGER) --autopkg -DclassName=Convolution${Type}${rank}D -Drank=${rank} -Dtype=${type} $< $@
//#     end
//# end

//# for typeId in ${FLOAT} ${DOUBLE}
//#     def type = ${}{type_${typeId}}
//#     def type = ${type}
//#     def Type = ${}{Type_${typeId}}
//#     def Type = ${Type}
//#     def TYPE = ${}{TYPE_${typeId}}
//#     def TYPE = ${TYPE}
$(CONVOLUTION_DIR)WeightedConvolution${Type}.java: WeightedConvolutionType.javax common.javax
	$(CODGER) --autopkg -DclassName=WeightedConvolution${Type} -Dtype=${type} $< $@

//#     for rank in 1:3
$(CONVOLUTION_DIR)WeightedConvolution${Type}${rank}D.java: WeightedConvolutionTypeRank.javax common.javax
	$(CODGER) --autopkg -DclassName=WeightedConvolution${Type}${rank}D -Drank=${rank} -Dtype=${type} $< $@
//#     end
//# end

#-----------------------------------------------------------------------------
# Array

all-array: $(ARRAY_OUTPUTS)

$(ARRAY)Scalar.java: $(ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Drank=0 $< $@

//# def rank = 1
//# while ${rank} <= 9
$(ARRAY)Array${rank}D.java: $(ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Drank=${rank} $< $@

//#     eval rank += 1
//# end
//#
//#
//# for typeId in ${BYTE}:${DOUBLE}
//#     def type = ${}{type_${typeId}}
//#     def type = ${type}
//#     def Type = ${}{Type_${typeId}}
//#     def Type = ${Type}
//#     def TYPE = ${}{TYPE_${typeId}}
//#     def TYPE = ${TYPE}
#-----------------------------------------------------------------------------
# ${Type}

all-${type}: $(${TYPE}_OUTPUTS)

$(ARRAY)${Type}Array.java: $(TYPED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=${type} $< $@

$(ARRAY)${Type}Scalar.java: $(TYPE_SCALAR_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=${type} $< $@

//#
//#     for rank in 1:9
$(ARRAY)${Type}${rank}D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=${type} -Drank=${rank} $< $@

//#     end
//#
//#     for rank in 1:9
$(ARRAY_IMPL)Flat${Type}${rank}D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=${type} -Drank=${rank} $< $@

//#     end
//#
//#     for rank in 1:9
$(ARRAY_IMPL)Selected${Type}${rank}D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=${type} -Drank=${rank} $< $@

//#     end
//#
//#     for rank in 1:9
$(ARRAY_IMPL)Stridden${Type}${rank}D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=${type} -Drank=${rank} $< $@

//#     end
//# end // loop over typeId

#-----------------------------------------------------------------------------
# Scanners, functions, etc.

all-mapping: $(MAPPING_OUTPUTS)

//# for pass in 1:2
//#     if ${pass} == 1
//#         def Class = Scanner
//#         def CLASS = SCANNER
//#     else
//#         def Class = Function
//#         def CLASS = FUNCTION
//#     end
//#     for typeId in ${BYTE}:${DOUBLE}
//#         def type = ${}{type_${typeId}}
//#         def type = ${type}
//#         def Type = ${}{Type_${typeId}}
//#         def Type = ${Type}
$(MAPPING)${Type}${Class}.java: $(${CLASS}_INPUTS)
	$(CODGER) --autopkg -Dtype=${type} $< $@

//#     end
//# end
#-----------------------------------------------------------------------------
