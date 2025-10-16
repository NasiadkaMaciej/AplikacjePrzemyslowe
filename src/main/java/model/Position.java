package MaciejNasiadka.Zadanie1;

import java.math.BigDecimal;

public enum Position {
	PREZES("Prezes", BigDecimal.valueOf(25_000), 1),
	WICEPREZES("Wiceprezes", BigDecimal.valueOf(18_000), 2),
	MANAGER("Manager", BigDecimal.valueOf(12_000), 3),
	PROGRAMISTA("Programista", BigDecimal.valueOf(8_000), 4),
	STAZYSTA("Stażysta", BigDecimal.valueOf(3_000), 5);

	private final String displayName;
	private final BigDecimal baseSalary;
	private final int hierarchyLevel;

	Position(String displayName, BigDecimal baseSalary, int hierarchyLevel) {
		this.displayName = displayName;
		this.baseSalary = baseSalary;
		this.hierarchyLevel = hierarchyLevel;
	}

	public String getDisplayName() { return displayName; }

	public BigDecimal getBaseSalary() { return baseSalary; }

	public int getHierarchyLevel() { return hierarchyLevel; }
}