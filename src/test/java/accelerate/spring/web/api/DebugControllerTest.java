package accelerate.spring.web.api;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import accelerate.spring.web.WebConfigProps;

/**
 * {@link Test} class for {@link DebugController}
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since August 21, 2019
 */
@SpringBootTest
@AutoConfigureMockMvc
class DebugControllerTest {
	/**
	 * {@link WebConfigProps} instance
	 */
	@Autowired
	private WebConfigProps webConfigProps = null;

	/**
	 * {@link MockMvc} instance to test API
	 */
	@Autowired
	private MockMvc mockMvc = null;

	/**
	 * Test method for {@link DebugController#debugRequest(HttpServletRequest)}.
	 * 
	 * @throws Exception
	 */
	@Test
	void testDebugRequest() throws Exception {
		this.mockMvc
				.perform(MockMvcRequestBuilders
						.get(this.webConfigProps.getWebAPIPathPrefix() + "/debug/request?paramA=valueA")
						.requestAttr("paramB", "valueB").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.details.contextPath", new Object[] {}).value("/accelerate-spring"))
				.andExpect(jsonPath("$.params.paramA", new Object[] {}).value("valueA"))
				.andExpect(jsonPath("$.attributes.paramA", new Object[] {}).exists());
	}

	/**
	 * Test method for {@link DebugController#debugSession(HttpServletRequest)}.
	 * 
	 * @throws Exception
	 */
	@Test
	void testDebugSession() throws Exception {
		MockHttpSession session = new MockHttpSession();
		session.setAttribute("paramA", "valueA");

		this.mockMvc
				.perform(MockMvcRequestBuilders.get(this.webConfigProps.getWebAPIPathPrefix() + "/debug/session")
						.session(session).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id", new Object[] {}).value(session.getId()))
				.andExpect(jsonPath("$.attributes.paramA", new Object[] {}).exists());
	}

	/**
	 * Test method for
	 * {@link DebugController#debugAttribute(HttpServletRequest, String)}.
	 * 
	 * @throws Exception
	 */
	@Test
	void testDebugAttribute() throws Exception {
		this.mockMvc
				.perform(MockMvcRequestBuilders
						.get(this.webConfigProps.getWebAPIPathPrefix() + "/debug/attribute/paramA")
						.requestAttr("paramA", "valueA").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.request", new Object[] {}).value("valueA"))
				.andExpect(jsonPath("$.session", new Object[] {}).value("NO_SESSION"));
	}
}