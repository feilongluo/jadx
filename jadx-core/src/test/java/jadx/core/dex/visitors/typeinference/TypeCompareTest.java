package jadx.core.dex.visitors.typeinference;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jadx.api.JadxArgs;
import jadx.core.dex.instructions.args.ArgType;
import jadx.core.dex.nodes.RootNode;

import static jadx.core.dex.instructions.args.ArgType.BOOLEAN;
import static jadx.core.dex.instructions.args.ArgType.CHAR;
import static jadx.core.dex.instructions.args.ArgType.INT;
import static jadx.core.dex.instructions.args.ArgType.NARROW;
import static jadx.core.dex.instructions.args.ArgType.NARROW_INTEGRAL;
import static jadx.core.dex.instructions.args.ArgType.OBJECT;
import static jadx.core.dex.instructions.args.ArgType.UNKNOWN;
import static jadx.core.dex.instructions.args.ArgType.UNKNOWN_OBJECT;
import static jadx.core.dex.instructions.args.ArgType.array;
import static org.assertj.core.api.Assertions.assertThat;

public class TypeCompareTest {

	private static final Logger LOG = LoggerFactory.getLogger(TypeCompareTest.class);
	private TypeCompare compare;

	@Before
	public void init() {
		JadxArgs args = new JadxArgs();
		RootNode root = new RootNode(args);
		root.load(Collections.emptyList());
		root.initClassPath();
		compare = new TypeCompare(root);
	}

	@Test
	public void compareTypes() {
		firstIsNarrow(INT, UNKNOWN);

		firstIsNarrow(BOOLEAN, INT);

		firstIsNarrow(array(UNKNOWN), UNKNOWN);
		firstIsNarrow(array(UNKNOWN), NARROW);
	}

	@Test
	public void comparePrimitives() {
		check(INT, UNKNOWN_OBJECT, TypeCompareEnum.CONFLICT);
		check(INT, OBJECT, TypeCompareEnum.CONFLICT);
		check(INT, CHAR, TypeCompareEnum.WIDER);

		firstIsNarrow(CHAR, NARROW_INTEGRAL);
		firstIsNarrow(array(CHAR), UNKNOWN_OBJECT);
	}

	@Test
	public void compareArrays() {
		firstIsNarrow(array(CHAR), OBJECT);
		firstIsNarrow(array(CHAR), array(UNKNOWN));
	}

	@Test
	public void compareGenerics() {
		ArgType mapCls = ArgType.object("java.util.Map");
		ArgType setCls = ArgType.object("java.util.Set");

		ArgType keyType = ArgType.genericType("K");
		ArgType valueType = ArgType.genericType("V");
		ArgType mapGeneric = ArgType.generic(mapCls.getObject(), new ArgType[]{keyType, valueType});

		check(mapGeneric, mapCls, TypeCompareEnum.NARROW_BY_GENERIC);
		check(mapCls, mapGeneric, TypeCompareEnum.WIDER_BY_GENERIC);

		check(mapCls, setCls, TypeCompareEnum.CONFLICT);
	}

	@Test
	public void compareGenericTypes() {
		ArgType vType = ArgType.genericType("V");
		ArgType rType = ArgType.genericType("R");

		check(vType, ArgType.OBJECT, TypeCompareEnum.NARROW_BY_GENERIC);
		check(ArgType.OBJECT, vType, TypeCompareEnum.WIDER_BY_GENERIC);

		check(vType, rType, TypeCompareEnum.CONFLICT);
		check(vType, vType, TypeCompareEnum.EQUAL);

		ArgType tType = ArgType.genericType("T");
		tType.setExtendTypes(Collections.singletonList(ArgType.STRING));

		check(tType, ArgType.STRING, TypeCompareEnum.NARROW_BY_GENERIC);
		check(ArgType.STRING, tType, TypeCompareEnum.WIDER_BY_GENERIC);

		check(tType, ArgType.OBJECT, TypeCompareEnum.NARROW_BY_GENERIC);
		check(ArgType.OBJECT, tType, TypeCompareEnum.WIDER_BY_GENERIC);

		// TODO: use extend types from generic declaration for more strict checks
//		check(vType, ArgType.STRING, TypeCompareEnum.CONFLICT);
//		check(ArgType.STRING, vType, TypeCompareEnum.CONFLICT);
	}

	private void firstIsNarrow(ArgType first, ArgType second) {
		check(first, second, TypeCompareEnum.NARROW);
		// reverse
		check(second, first, TypeCompareEnum.WIDER);
	}

	private void check(ArgType first, ArgType second, TypeCompareEnum expectedResult) {
		TypeCompareEnum result = compare.compareTypes(first, second);
		assertThat(result).as("Compare '%s' vs '%s'", first, second).isEqualTo(expectedResult);
	}
}
