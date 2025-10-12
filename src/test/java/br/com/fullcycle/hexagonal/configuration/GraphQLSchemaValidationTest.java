package br.com.fullcycle.hexagonal.configuration;

import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.junit.jupiter.api.Test;
import java.io.File;

class GraphQLSchemaValidationTest {
    @Test
    void validateSchema() {
        File schemaFile = new File("src/main/resources/graphql/schema.gqls");
        TypeDefinitionRegistry registry = new SchemaParser().parse(schemaFile);
    }
}
