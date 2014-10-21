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
CODGER =./tpp

# Directories:
TOP = ../mitiv
ARRAY = $(TOP)/array/
ARRAY_IMPL = $(TOP)/array/impl/
BASE = $(TOP)/base/

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
               $(BASE)Shape.java

BYTE_OUTPUTS = $(subst @TYPE@,Byte,$(TYPED_OUTPUTS))

SHORT_OUTPUTS = $(subst @TYPE@,Short,$(TYPED_OUTPUTS))

INT_OUTPUTS = $(subst @TYPE@,Int,$(TYPED_OUTPUTS))

LONG_OUTPUTS = $(subst @TYPE@,Long,$(TYPED_OUTPUTS))

FLOAT_OUTPUTS = $(subst @TYPE@,Float,$(TYPED_OUTPUTS))

DOUBLE_OUTPUTS = $(subst @TYPE@,Double,$(TYPED_OUTPUTS))

ARRAY_INPUTS = ArrayND.javax common.javax

TYPED_ARRAY_INPUTS = TypeArray.javax common.javax

TYPE_RANK_INPUTS = TypeND.javax common.javax

TYPE_SCALAR_INPUTS = TypeScalar.javax common.javax

ARRAY_IMPL_INPUTS = commonImpl.javax commonLoops.javax common.javax

FLAT_ARRAY_INPUTS = FlatArray.javax $(ARRAY_IMPL_INPUTS)

STRIDDEN_ARRAY_INPUTS = StriddenArray.javax $(ARRAY_IMPL_INPUTS)

SELECTED_ARRAY_INPUTS = SelectedArray.javax $(ARRAY_IMPL_INPUTS)

default:

all: all-array all-byte all-short all-int all-long all-float all-double \
     all-misc

clean:
	rm -f *~

#-----------------------------------------------------------------------------
# Miscellaneaous

all-misc: $(MISC_OUTPUTS)

Makefile: Makefile.x
	$(CODGER) $< $@

$(ARRAY)ArrayFactory.java: ArrayFactory.javax
	$(CODGER) -Dpackage=mitiv.array $< $@

$(BASE)Shape.java: Shape.javax
	$(CODGER) -Dpackage=mitiv.array $< $@

$(ARRAY)ArrayUtils.java: ArrayUtils.javax common.javax
	$(CODGER) -Dpackage=mitiv.array $< $@

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
//# def typeId = ${BYTE}
//# while ${typeId} <= ${DOUBLE}
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

//#     def rank = 0
//#     while ${rank} < 9
//#         eval rank += 1
$(ARRAY)${Type}${rank}D.java: $(TYPE_RANK_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=${type} -Drank=${rank} $< $@

//#     end
//#     def rank = 0
//#     while ${rank} < 9
//#         eval rank += 1
$(ARRAY_IMPL)Flat${Type}${rank}D.java: $(FLAT_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=${type} -Drank=${rank} $< $@

//#     end
//#     def rank = 0
//#     while ${rank} < 9
//#         eval rank += 1
$(ARRAY_IMPL)Selected${Type}${rank}D.java: $(SELECTED_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=${type} -Drank=${rank} $< $@

//#     end
//#     def rank = 0
//#     while ${rank} < 9
//#         eval rank += 1
$(ARRAY_IMPL)Stridden${Type}${rank}D.java: $(STRIDDEN_ARRAY_INPUTS)
	$(CODGER) -Dpackage=mitiv.array -Dtype=${type} -Drank=${rank} $< $@

//#     end
//#     eval typeId += 1
//# end // loop over typeId

#-----------------------------------------------------------------------------
