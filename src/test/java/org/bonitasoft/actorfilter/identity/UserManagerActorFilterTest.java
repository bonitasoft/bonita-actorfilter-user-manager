package org.bonitasoft.actorfilter.identity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.bonitasoft.actorfilter.identity.UserManagerActorFilter.USER_ID;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bonitasoft.engine.api.APIAccessor;
import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.connector.ConnectorValidationException;
import org.bonitasoft.engine.identity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserManagerActorFilterTest {

    @InjectMocks
    private UserManagerActorFilter filter;

    @Mock(lenient = true)
    private APIAccessor apiAccessor;
    @Mock(lenient = true)
    private IdentityAPI identityAPI;

    @BeforeEach
    void setUp() {
        when(apiAccessor.getIdentityAPI()).thenReturn(identityAPI);
    }

    @Test
    void should_throw_exception_if_mandatory_input_is_missing() {
        assertThrows(ConnectorValidationException.class, () ->
                filter.validateInputParameters()
        );
    }

    @Test
    void should_throw_exception_if_mandatory_input_is_not_positive_long() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(USER_ID, -1L);
        filter.setInputParameters(parameters);
        assertThrows(ConnectorValidationException.class, () ->
                filter.validateInputParameters()
        );
    }

    @Test
    void should_throw_exception_if_mandatory_input_is_zero() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(USER_ID, 0L);
        filter.setInputParameters(parameters);
        assertThrows(ConnectorValidationException.class, () ->
                filter.validateInputParameters()
        );
    }

    @Test
    void should_throw_exception_if_mandatory_input_is_not_a_long() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(USER_ID, "1");
        filter.setInputParameters(parameters);
        assertThrows(ConnectorValidationException.class, () ->
                filter.validateInputParameters()
        );
    }

    @Test
    void should_return_a_list_of_candidates() throws Exception {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        long userId = 3L;
        parameters.put(USER_ID, userId);
        filter.setInputParameters(parameters);

        User user = mock(User.class);
        when(identityAPI.getUser(eq(userId))).thenReturn(user);
        long managerId = 666;
        when(user.getManagerUserId()).thenReturn(managerId);

        // When
        List<Long> candidates = filter.filter("MyActor");

        // Then
        assertThat(candidates).as("Only the specified user's manager Id can be candidate.")
                .containsExactly(managerId);

    }

}
