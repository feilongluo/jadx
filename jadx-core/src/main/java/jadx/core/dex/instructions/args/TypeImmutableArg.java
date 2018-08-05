package jadx.core.dex.instructions.args;

import org.jetbrains.annotations.NotNull;

import jadx.core.dex.visitors.typeinference.TypeInfo;

public class TypeImmutableArg extends RegisterArg {

	public TypeImmutableArg(int rn, ArgType type) {
		super(rn, type);
	}

	@Override
	public boolean isTypeImmutable() {
		return true;
	}

	@Override
	public void setType(ArgType type) {
		// ignore
	}

	@Override
	public ArgType getType() {
		return type;
	}

	@Override
	void setSVar(@NotNull SSAVar sVar) {
		TypeInfo typeInfo = sVar.getTypeInfo();
		typeInfo.setTypeImmutable(true);
		typeInfo.setType(type);
		super.setSVar(sVar);
	}
}
