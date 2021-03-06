public class PolicySet_ReadWrite extends PolicySet {
	public PolicySet_ReadWrite(){
		addId("ReadWrite_Policy");
		addCombiningAlg(it.unifi.facpl.lib.algorithm.DenyUnlessPermitGreedy.class);
		ExpressionFunction e1 = new ExpressionFunction(it.unifi.facpl.lib.function.comparison.Equal.class, "Bob",
				new AttributeName("name", "id"));
		ExpressionFunction e2 = new ExpressionFunction(it.unifi.facpl.lib.function.comparison.Equal.class, "Alice",
				new AttributeName("name", "id"));
		ExpressionBooleanTree ebt = new ExpressionBooleanTree(ExprBooleanConnector.OR, e1, e2);
		addTarget(ebt);
		addPolicyElement(new PolicySet_Write());
		addPolicyElement(new PolicySet_Read());
		addPolicyElement(new PolicySet_StopWrite());
		addPolicyElement(new PolicySet_StopRead());
	}

	private class PolicySet_Write extends PolicySet {
		public PolicySet_Write() {
			addId("Write_Policy");
			addCombiningAlg(
				it.unifi.facpl.lib.algorithm.DenyUnlessPermitGreedy.class);
			ExpressionFunction e1 = new ExpressionFunction(comparison.Equal.class, 
					"file1",
					new AttributeName("file", "id")
					);
			ExpressionFunction e2 = new ExpressionFunction(comparison.Equal.class, 
					"write",
					new AttributeName("action", "id")
					);
			ExpressionBooleanTree ebt = new ExpressionBooleanTree(ExprBooleanConnector.AND, e1, e2);
			addTarget(ebt);
			addPolicyElement(new Rule_write());
			addObligation(new ObligationStatus(new FlagStatus(), Effect.PERMIT, ObligationType.M,
					new StatusAttribute("isWriting", FacplStatusType.BOOLEAN), true));
		}
		private class Rule_write extends Rule {
			Rule_write() {
				addId("write");
				addEffect(Effect.PERMIT);
				ExpressionFunction e1=
				new ExpressionFunction(comparison.Equal.class,
										new StatusAttribute("isWriting", FacplStatusType.BOOLEAN),
						false);
				ExpressionFunction e2=
				new ExpressionFunction(comparison.Equal.class,
									new StatusAttribute("counterReadFile1", FacplStatusType.INT),
						0);
				ExpressionBooleanTree ebt = 
					new ExpressionBooleanTree(ExprBooleanConnector.AND, e1, e2);
				addTarget(ebt);
			}
		}
	}

	private class PolicySet_Read extends PolicySet {


		public PolicySet_Read()  {

			addId("Read_Policy");
			// Algorithm Combining
			addCombiningAlg(it.unifi.facpl.lib.algorithm.DenyUnlessPermitGreedy.class);
			// Target
			ExpressionFunction e1 = new ExpressionFunction(it.unifi.facpl.lib.function.comparison.Equal.class, 
					"file1",
					new AttributeName("file", "id")
					);
			ExpressionFunction e2 = new ExpressionFunction(it.unifi.facpl.lib.function.comparison.Equal.class, 
					"read",
					new AttributeName("action", "id")
					);
			ExpressionBooleanTree ebt = new ExpressionBooleanTree(ExprBooleanConnector.AND, e1, e2);
			addTarget(ebt);
			// PolElements
			addPolicyElement(new Rule_read());
			// Obligation
			addObligation(new ObligationStatus(new AddStatus(), Effect.PERMIT, ObligationType.M,
					new StatusAttribute("counterReadFile1", FacplStatusType.INT), 1));
		}

		private class Rule_read extends Rule {

			Rule_read() {
				addId("read");
				// Effect
				addEffect(Effect.PERMIT);
				ExpressionFunction e1 = new ExpressionFunction(it.unifi.facpl.lib.function.comparison.Equal.class,
						new StatusAttribute("isWriting", FacplStatusType.BOOLEAN),
						false);//nessuno scrive
				ExpressionFunction e2 = new ExpressionFunction(it.unifi.facpl.lib.function.comparison.LessThan.class,
						new StatusAttribute("counterReadFile1", FacplStatusType.INT),
						2);//i lettori sono meno di due
				
				ExpressionBooleanTree ebt = new ExpressionBooleanTree(ExprBooleanConnector.AND, e1, e2);
				addTarget(ebt);
			}
		}

	}
	
	private class PolicySet_StopRead extends PolicySet {


		public PolicySet_StopRead() {

			addId("StopRead_Policy");
			// Algorithm Combining
			addCombiningAlg(it.unifi.facpl.lib.algorithm.DenyUnlessPermitGreedy.class);
			// Target
			ExpressionFunction e1 = new ExpressionFunction(it.unifi.facpl.lib.function.comparison.Equal.class, 
					"file1",
					new AttributeName("file", "id")
					);
			ExpressionFunction e2 = new ExpressionFunction(it.unifi.facpl.lib.function.comparison.Equal.class, 
					"stopRead",
					new AttributeName("action", "id")
					);
			ExpressionBooleanTree ebt = new ExpressionBooleanTree(ExprBooleanConnector.AND, e1, e2);
			addTarget(ebt);
			// PolElements
			addPolicyElement(new Rule_stopRead());
			// Obligation
			addObligation(new ObligationStatus(new SubStatus(), Effect.PERMIT, ObligationType.M,
							new StatusAttribute("counterReadFile1", FacplStatusType.INT), 1));//meno uno sui lettori
		}

		private class Rule_stopRead extends Rule {

			Rule_stopRead() {
				addId("stopRead");
				// Effect
				addEffect(Effect.PERMIT);
				addTarget(new ExpressionFunction(it.unifi.facpl.lib.function.comparison.GreaterThan.class,
						new StatusAttribute("counterReadFile1", FacplStatusType.INT),
						0));//solo se ci sono lettori
			}
		}
	}
	
	private class PolicySet_StopWrite extends PolicySet {


		public PolicySet_StopWrite(){
			addId("StopWrite_Policy");
			// Algorithm Combining
			addCombiningAlg(it.unifi.facpl.lib.algorithm.DenyUnlessPermitGreedy.class);
			// Target
			ExpressionFunction e1 = new ExpressionFunction(it.unifi.facpl.lib.function.comparison.Equal.class, 
					"file1",
					new AttributeName("file", "id")
					);
			ExpressionFunction e2 = new ExpressionFunction(it.unifi.facpl.lib.function.comparison.Equal.class, 
					"stopWrite",
					new AttributeName("action", "id")
					);
			ExpressionBooleanTree ebt = new ExpressionBooleanTree(ExprBooleanConnector.AND, e1, e2);
			addTarget(ebt);
			// PolElements
			addPolicyElement(new Rule_write());
			// Obligation
			addObligation(new ObligationStatus(new FlagStatus(), Effect.PERMIT, ObligationType.M,
							new StatusAttribute("isWriting", FacplStatusType.BOOLEAN), false));
		}

		private class Rule_write extends Rule {

			Rule_write() {
				addId("stopWrite");
				// Effect
				addEffect(Effect.PERMIT);
				addTarget(new ExpressionFunction(it.unifi.facpl.lib.function.comparison.Equal.class,
							new StatusAttribute("isWriting", FacplStatusType.BOOLEAN),
						true));
			}
		}
	}
}
