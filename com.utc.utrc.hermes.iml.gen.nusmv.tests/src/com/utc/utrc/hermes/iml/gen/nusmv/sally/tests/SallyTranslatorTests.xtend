package com.utc.utrc.hermes.iml.gen.nusmv.sally.tests

import org.junit.runner.RunWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.InjectWith
import com.utc.utrc.hermes.iml.tests.ImlInjectorProvider
import com.google.inject.Inject
import com.utc.utrc.hermes.iml.ImlParseHelper
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import com.utc.utrc.hermes.iml.tests.TestHelper
import org.junit.Test
import com.utc.utrc.hermes.iml.util.FileUtil
import com.utc.utrc.hermes.iml.iml.Model
import com.utc.utrc.hermes.iml.gen.nusmv.systems.Systems
import com.utc.utrc.hermes.iml.gen.nusmv.generator.Configuration
import com.utc.utrc.hermes.iml.gen.nusmv.generator.NuSmvGenerator
import com.utc.utrc.hermes.iml.gen.nusmv.sms.Sms
import com.utc.utrc.hermes.iml.gen.nusmv.model.NuSmvModel
import com.utc.utrc.hermes.iml.iml.NamedType
import com.utc.utrc.hermes.iml.util.ImlUtil
import com.utc.utrc.hermes.iml.custom.ImlCustomFactory
import com.utc.utrc.hermes.iml.gen.nusmv.generator.NuSmvGeneratorServices
import com.utc.utrc.hermes.iml.gen.nusmv.sally.generator.SallyGenerator
import com.utc.utrc.hermes.iml.gen.nusmv.sally.generator.SallyGeneratorServices

@RunWith(XtextRunner)
@InjectWith(ImlInjectorProvider)
class SallyTranslatorTests {
	
	@Inject extension ImlParseHelper
	
	@Inject extension ValidationTestHelper
	
	@Inject extension TestHelper
	
	@Inject 
	Systems sys ;
	
	@Inject 
	Sms sms ;
	
	@Inject
	SallyGenerator gen ;
	
	@Inject
	SallyGeneratorServices generatorServices;
	
		
	@Test
	def void testTranslation() {
		
		var Model m = parse(FileUtil.readFileContent("models/fromaadl/UxASRespondsEvents_pkg.iml"),true) ;
		sys.process(m) ;
		System.out.println(sys.toString)
		sms.systems = sys;
		sms.process(m);
		gen.sms = sms;
		var NamedType smtype = m.findSymbol("UxAS_responds_dot_i") as NamedType;
		gen.generateStateMachine("top",sms.getStateMachine(ImlCustomFactory.INST.createSimpleTypeReference(smtype))) ;
		var sally = gen.model ;
		System.out.println(sally.toString)
		System.out.println(prettify(gen.serializeModel));
		
	}
	
	def prettify(String sexpr) {
        return sexpr.replaceAll("((\\)[^\\(]*)+)", "$1\n");
    }
}

