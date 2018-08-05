package jadx.core.dex.visitors.typeinference;

import java.util.HashSet;
import java.util.Set;

import jadx.core.dex.instructions.args.ArgType;

public class TypeInfo {
	private ArgType type = ArgType.UNKNOWN;
	private boolean typeImmutable;

	private final Set<ITypeBound> bounds = new HashSet<>();

	public ArgType getType() {
		return type;
	}

	public void setType(ArgType type) {
		this.type = type;
	}

	public Set<ITypeBound> getBounds() {
		return bounds;
	}

	public boolean isTypeImmutable() {
		return typeImmutable;
	}

	public void setTypeImmutable(boolean typeImmutable) {
		this.typeImmutable = typeImmutable;
	}

	@Override
	public String toString() {
		return "TypeInfo{" +
				"type=" + type +
				", bounds=" + bounds +
				(typeImmutable ? ", immutable" : "") +
				'}';
	}
}
